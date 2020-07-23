package thang.com.uptimum.Main;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import thang.com.uptimum.Dialog.CommentBottomSheetDialog;
import thang.com.uptimum.Dialog.DialogShowImageStatus;
import thang.com.uptimum.Main.other.ViewpagerStoriesActivity;
import thang.com.uptimum.R;
import thang.com.uptimum.adapter.postsAdapter;
import thang.com.uptimum.adapter.storyAdapter;
import thang.com.uptimum.model.Posts;
import thang.com.uptimum.model.Story;
import thang.com.uptimum.network.NetworkUtil;
import thang.com.uptimum.network.PostsRetrofit;
import thang.com.uptimum.network.StoryRetrofit;
import thang.com.uptimum.upload.UploadPostsActivity;
//import thang.com.uptimum.upload.UploadPostsActivity;
//import static thang.com.uptimum.Main.MainActivity.relativeLayout;
import static thang.com.uptimum.Socket.SocketIO.socket;
import static thang.com.uptimum.util.Constants.BASE_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener , ComponentCallbacks2 {
    private final String TAG = "HomeFragment";

    private SwipeRefreshLayout swipe_refresh_layout;
    private ShimmerLayout shimmerLayout;
    private View contactsview;
    private CircleImageView imguser, imgAvataUserLogin;
    private TextView txtstatus, txtThongbao, txtUserNameLogin;
    private RoundedImageView  ImgstoriesUserLogin;
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerViewStory, recyclerViewstatus;
    private ImageView addStories;
    private LinearLayoutManager linearLayoutManagerstatus, linearLayoutManagerstory;
    private ArrayList<Posts> arrayPosts;
    private ArrayList<Story> arrayStory;
    private postsAdapter adapterPosts;
    private storyAdapter adapterStory;
    private FrameLayout homeFragment;

    private NetworkUtil networkUtil;
    private PostsRetrofit postsRetrofit;
    private StoryRetrofit storyRetrofit;
    private Retrofit retrofit;

    private BottomSheetBehavior mBehavior;

    private SharedPreferences sessionManagement;
    private String id ="";
    private String avata = "";
    private String coverimage = "";
    private String username = "";
    private int numberClickStory = 0;

    private postsAdapter.RecyclerviewClickListener listener;



    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
        contactsview =  inflater.inflate(R.layout.fragment_home, container, false);
//        // thêm tiếp nghe ba
        homeFragment = (FrameLayout) contactsview.findViewById(R.id.homeFragment);
        nestedScrollView = (NestedScrollView) contactsview.findViewById(R.id.nestedScroolView);
        imguser = (CircleImageView) contactsview.findViewById(R.id.user);
        txtstatus = (TextView) contactsview.findViewById(R.id.status);
        txtThongbao = (TextView) contactsview.findViewById(R.id.txtThongbao);
        shimmerLayout = (ShimmerLayout) contactsview.findViewById(R.id.shimmer_layout);
        swipe_refresh_layout = (SwipeRefreshLayout) contactsview.findViewById(R.id.swipe_refresh_layout);
        txtUserNameLogin = (TextView) contactsview.findViewById(R.id.txtUserNameLogin);
        imgAvataUserLogin = (CircleImageView) contactsview.findViewById(R.id.imgAvataUserLogin);
        ImgstoriesUserLogin = (RoundedImageView) contactsview.findViewById(R.id.ImgstoriesUserLogin);

        recyclerViewStory = (RecyclerView) contactsview.findViewById(R.id.recyclerViewStory);
        recyclerViewstatus = (RecyclerView) contactsview.findViewById(R.id.recyclerViewstatus);
        recyclerViewStory.setHasFixedSize(true);
        recyclerViewstatus.setHasFixedSize(true);
        linearLayoutManagerstory =  new LinearLayoutManager
                (getContext(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManagerstatus =  new LinearLayoutManager
                (getContext(), LinearLayoutManager.VERTICAL, true);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
//        recyclerViewstatus.addItemDecoration(dividerItemDecoration);
        recyclerViewStory.setLayoutManager(linearLayoutManagerstory);
        recyclerViewstatus.setLayoutManager(linearLayoutManagerstatus);
        recyclerViewstatus.setNestedScrollingEnabled(false);
        //
        //arr
        networkUtil = new NetworkUtil();
        retrofit = networkUtil.getRetrofit();
        addEvents();
        getStory();
        getPosts();
        addDataUserlogin();
        setOnClickListener();


        return contactsview;
//        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerViewStory.setAdapter(null); // will trigger the recycling in the adapter
        recyclerViewstatus.setAdapter(null);
    }

    public void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();

//        getFragmentManager().beginTransaction().addToBackStack(null).commit();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.status:
                addNewStatus();
                break;
            case R.id.ImgstoriesUserLogin:
                Intent intent = new Intent(getActivity().getApplicationContext(), ViewpagerStoriesActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("numberClickStory", 0);
                startActivity(intent);
                break;
            case R.id.user:
                Intent personal = new Intent(getActivity().getApplicationContext(), PersonalActivity.class);
                personal.putExtra("iduser", id);
                startActivity(personal);
                break;
            default:
                break;
        }
    }
    private void addEvents() {
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                txtThongbao.setVisibility(View.GONE);
                shimmerLayout.setVisibility(View.VISIBLE);
                recyclerViewStory.setAdapter(null); // will trigger the recycling in the adapter
                recyclerViewstatus.setAdapter(null);
                getStory();
                getPosts();
                freeMemory();
                swipe_refresh_layout.setRefreshing(false);
            }
        });
        txtstatus.setOnClickListener(this);
        ImgstoriesUserLogin.setOnClickListener(this);
        imguser.setOnClickListener(this);
    }

    private void addNewStatus() {
        Intent intent = new Intent(getContext(), UploadPostsActivity.class);
        startActivity(intent);
    }

    private void addDataUserlogin() {
        // get thông tin
        sessionManagement = getContext().getApplicationContext().getSharedPreferences("userlogin",Context.MODE_PRIVATE);
        id = sessionManagement.getString("id","");
        avata = sessionManagement.getString("avata", "");
        coverimage = sessionManagement.getString("coverimage", "");
        username = sessionManagement.getString("username","");
        Log.d(TAG," "+id+avata+coverimage+username);
        //set thông tin
        Picasso.get().load(BASE_URL+"uploads/"+avata).into(imguser);
        txtstatus.setHint(username+" đang nghĩ gì ?");
        socket.emit("chat message", id);
    }

    private void getStory() {

        arrayStory = new ArrayList<>();
        shimmerLayout.startShimmerAnimation();
        arrayStory.clear();
        storyRetrofit = retrofit.create(StoryRetrofit.class);
        Call<List<Story>> callstory = storyRetrofit.getStory();
        callstory.enqueue(new Callback<List<Story>>() {
            @Override
            public void onResponse(Call<List<Story>> call, Response<List<Story>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "lỗi rác story", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Story> storys = response.body();
                arrayStory.clear();
                for(Story story : storys){
                    if(story.getUsers().getId().equals(id)){ // lấy story userLogin để đầu mảng
                        Picasso.get().load(BASE_URL+"uploads/"+story.getFile()[0]).into(ImgstoriesUserLogin);
                        Picasso.get().load(BASE_URL+"uploads/"+story.getUsers().getAvata()).into(imgAvataUserLogin);
                        Log.d("avataa", " "+story.getUsers().getAvata());
                        txtUserNameLogin.setText(story.getUsers().getUsername());
                    }else{
                        arrayStory.add(story);
                    }
                }

                adapterStory = new storyAdapter(arrayStory, getActivity().getApplicationContext());
                recyclerViewStory.setAdapter(adapterStory);

                shimmerLayout.stopShimmerAnimation();
                nestedScrollView.setVisibility(View.VISIBLE);
                shimmerLayout.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Story>> call, Throwable t) {
                Log.d("loaddataa","Load không được lỗi : "+t.getMessage());
                shimmerLayout.setVisibility(View.GONE);
                txtThongbao.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getPosts() {

        arrayPosts = new ArrayList<>();
        postsRetrofit = retrofit.create(PostsRetrofit.class);
        arrayPosts.clear();
        Call<List<Posts>> callposts = postsRetrofit.getPosts();
        callposts.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "lỗi rác posts", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Posts> posts = response.body();
                for(Posts post : posts){
                    arrayPosts.add(post);
                }
//                Collections.reverse(arrayPosts);

                adapterPosts = new postsAdapter(arrayPosts, getActivity().getApplicationContext(), listener);
                adapterPosts.notifyDataSetChanged();
                recyclerViewstatus.setAdapter(adapterPosts);
            }

            @Override
            public void onFailure(Call<List<Posts>> call, Throwable t) {
                Log.d("lỗi posts",t.getMessage(),t);
            }
        });
    }
    private void setOnClickListener(){
        listener = new postsAdapter.RecyclerviewClickListener() {
            @Override
            public void onClickComment(RelativeLayout btnComment, int position, int typeClick) {

                    Log.d("kjqhwekwqe", " " + position);
                    CommentBottomSheetDialog commentBottomSheetDialog = new
                            CommentBottomSheetDialog(arrayPosts.get(position).getId());
                    commentBottomSheetDialog.show(getFragmentManager(),
                            "add_photo_dialog_fragment");

            }

            @Override
            public void showImg(ImageView imgShow, int position, int typeClick) {
                DialogShowImageStatus dialogShowImageStatus = new DialogShowImageStatus(position, arrayPosts);
                dialogShowImageStatus.show(getFragmentManager(),"ShowImg_dialog_fragment");
            }
        };
    }

    @Override
    public void onTrimMemory(int level) {
        switch (level) {

            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:


                break;

            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:


                break;

            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                break;

            default:

                break;
        }
    }
    private void unbindDrawables(View view) {
        if (view.getBackground() != null)
            view.getBackground().setCallback(null);

        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setImageBitmap(null);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++)
                unbindDrawables(viewGroup.getChildAt(i));

            if (!(view instanceof AdapterView))
                viewGroup.removeAllViews();
        }
    }
}