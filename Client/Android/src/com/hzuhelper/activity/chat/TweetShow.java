package com.hzuhelper.activity.chat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzuhelper.R;
import com.hzuhelper.activity.BaseActivity;
import com.hzuhelper.adapter.CommentAdapter;
import com.hzuhelper.config.StaticValues;
import com.hzuhelper.config.staticURL;
import com.hzuhelper.model.ChatComment1Info;
import com.hzuhelper.model.ChatTweetInfo;
import com.hzuhelper.tools.ConstantStrUtil;
import com.hzuhelper.tools.StringUtils;
import com.hzuhelper.web.ResultObj;
import com.hzuhelper.web.WebRequest;

public class TweetShow extends BaseActivity implements OnScrollListener,OnClickListener {
    private ChatTweetInfo               chatTweetInfo;
    private ListView                    listView;
    private SimpleDateFormat            sdf            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
    private boolean                     updateInFooter = false;
    private ProgressDialog              progressDialog;

    private ArrayList<ChatComment1Info> list1;
    private ArrayList<ChatComment1Info> list2;
    private LinearLayout                footView;
    private CommentAdapter              adapter;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_tweet_show);
        listView = (ListView)findViewById(R.id.listView1);
        findViewById(R.id.btn_left).setOnClickListener(this);
        init();
    }

    private void init(){
        progressDialog = ProgressDialog.show(TweetShow.this,null,"获取中...");
        chatTweetInfo = new ChatTweetInfo();
        chatTweetInfo.setId(Integer.valueOf(getIntent().getIntExtra("tweetId",-1)));
        list2 = new ArrayList<ChatComment1Info>();
        new Thread(new Runnable() {
            public void run(){
                if (!getData(chatTweetInfo.getId(),-1)) {
                    handler.sendEmptyMessage(GET_DATA_FAIL);
                    return;
                }
                handler.sendEmptyMessage(GER_DATA_SUCCESS);
            }
        }).start();
    }

    private void showComment(){
        list1 = new ArrayList<ChatComment1Info>(20);
        list1.addAll(list2);
        adapter = new CommentAdapter(TweetShow.this,list1,chatTweetInfo);
        listView.setAdapter(adapter);
        if (list2.size()<20) cancelUpdate();
        else {
            listView.setOnScrollListener(this);
            updateInFooter = true;
        }
        list2.clear();
    };

    private void addFootView(){
        footView = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.chat_tweet_listview_foot,null);
        listView.addFooterView(footView,null,false);
    }

    private void addHeadView(){
        LinearLayout lv_head = (LinearLayout)LayoutInflater.from(TweetShow.this).inflate(R.layout.chat_comment_listview_head,null);

        TextView tv_date = (TextView)lv_head.findViewById(R.id.tv_date);
        TextView tv_content = (TextView)lv_head.findViewById(R.id.tv_content);
        Button btn_comment = (Button)lv_head.findViewById(R.id.btn_comment);

        tv_date.setText(chatTweetInfo.getPublishDate());
        tv_content.setText(Html.fromHtml(StringUtils.tweetContentTran(chatTweetInfo.getContent())));
        if (chatTweetInfo.getCommentCount()>0) {
            btn_comment.setText(String.valueOf(chatTweetInfo.getComment_count()));
        } else {
            btn_comment.setText("评论");
        }
        btn_comment.setOnClickListener(this);

        listView.addHeaderView(lv_head);
    }

    private void getMoreDataSuccess(){
        list1.addAll(list2);
        adapter.notifyDataSetChanged();
        if (list2.size()<20) cancelUpdate();
        else updateInFooter = true;
        list2.clear();
    }

    private void cancelUpdate(){
        footView.setVisibility(View.GONE);
    }

    private boolean getData(int tweet_id,int comment_id){
        WebRequest wq = new WebRequest(staticURL.chat_comment_getlist1) {
            @Override
            protected void onFinished(ResultObj resultObj){

            }
        };
        wq.setParam(StaticValues.tweetId,String.valueOf(tweet_id));
        wq.setParam(StaticValues.commentId,String.valueOf(comment_id));
        wq.start();
        /*try {
            JSONObject jsonObject = new JSONObject(result);
            if (!jsonObject.getString(ConstantStrUtil.STR_STATU).equals(ConstantStrUtil.STR_TRUE)) { return false; }
            if (comment_id<1) {
                JSONObject jsonRsp1 = jsonObject.getJSONObject("tweet");
                jsonToTweetInfo(jsonRsp1);
            }
            JSONArray jsonRsp2 = jsonObject.getJSONArray("commentlist");
            jsonToList2(jsonRsp2);
            return true;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return false;
        } catch (ParseException ex) {
            ex.printStackTrace();
            return false;
        }*/
    }

    private void jsonToTweetInfo(JSONObject jsonRsp1) throws JSONException{
        chatTweetInfo.setId(jsonRsp1.getInt("id"));
        chatTweetInfo.setCommentCount(jsonRsp1.getInt("commentCount"));
        chatTweetInfo.setContent(jsonRsp1.getString("content"));
        chatTweetInfo.setPublish_date(jsonRsp1.getString("publishDatetime"));
    }

    private void jsonToList2(JSONArray jsonRsp) throws JSONException,ParseException{
        list2.clear();
        for (int i = 0, len = jsonRsp.length(); i<len; i++) {
            JSONObject json = jsonRsp.getJSONObject(i);
            ChatComment1Info chat_commentInfo = new ChatComment1Info();
            chat_commentInfo.setAuthor_id(json.getString("authorId"));
            chat_commentInfo.setId(json.getInt("id"));
            chat_commentInfo.setContent(json.getString("content"));
            chat_commentInfo.setTweet_id(json.getInt("tweetId"));
            chat_commentInfo.setStatu(json.getInt("statu"));
            chat_commentInfo.setResponse_user_id(json.getString("receiverId"));
            chat_commentInfo.setPublish_date(sdf.parse(json.getString("publishDatetime")));
            list2.add(chat_commentInfo);
        }
    }

    private final int GET_DATA_FAIL         = 1;
    private final int GER_DATA_SUCCESS      = 3;
    private final int GET_MORE_DATA_SUCCESS = 4;
    private Handler   handler               = new Handler() {
                                                public void handleMessage(Message msg){
                                                    switch (msg.what) {
                                                    case GET_DATA_FAIL:
                                                        Toast.makeText(TweetShow.this,"获取失败.",Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                        break;
                                                    case GER_DATA_SUCCESS:
                                                        addHeadView();
                                                        addFootView();
                                                        showComment();
                                                        progressDialog.dismiss();
                                                        break;
                                                    case GET_MORE_DATA_SUCCESS:
                                                        getMoreDataSuccess();
                                                        break;
                                                    default:
                                                        break;
                                                    }
                                                }

                                            };

    public void onScrollStateChanged(AbsListView view,int scrollState){
        if (scrollState==OnScrollListener.SCROLL_STATE_IDLE&&listView.getLastVisiblePosition()>view.getCount()-2&&updateInFooter) {
            updateInFooter = false;
            new Thread(new Runnable() {
                public void run(){
                    if (!getData(chatTweetInfo.getId(),list1.get(list1.size()-1).getId())) {
                        handler.sendEmptyMessage(GET_DATA_FAIL);
                        return;
                    }
                    handler.sendEmptyMessage(GET_MORE_DATA_SUCCESS);
                }
            }).start();
        }
    }

    public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount){}

    @Override
    public void onClick(View v){
        switch (v.getId()) {
        case R.id.btn_left:
            this.finish();
            break;
        case R.id.btn_comment:
            Intent intent = new Intent(TweetShow.this,CommentCommit.class);
            intent.putExtra("tweetId",chatTweetInfo.getId());
            startActivity(intent);
            break;
        }
    }

}
