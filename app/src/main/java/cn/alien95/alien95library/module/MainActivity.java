package cn.alien95.alien95library.module;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import alien95.cn.cellview.ui.LookImageActivity;
import alien95.cn.refreshrecyclerview.adapter.RecyclerAdapter;
import alien95.cn.refreshrecyclerview.callback.Action;
import alien95.cn.refreshrecyclerview.view.BaseViewHolder;
import alien95.cn.refreshrecyclerview.view.RefreshRecyclerView;
import cn.alien95.alien95library.R;
import cn.alien95.alien95library.model.APIService;
import cn.alien95.alien95library.model.ImageModel;
import cn.alien95.alien95library.model.bean.Image;
import cn.alien95.alien95library.model.bean.ImageRespond;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RefreshRecyclerView refreshRecyclerView;
    private MyAdapter adapter;
    private List<String> data = new ArrayList<>();
    private List<String> picUrlData = new ArrayList<>();
    private Intent intent;
    private String searchWord = "风景";
    private int pager = 1;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new MyAdapter(this);
        refreshRecyclerView = (RefreshRecyclerView) findViewById(R.id.refresh_recycler_view);
        refreshRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        refreshRecyclerView.setAdapter(adapter);

        intent = new Intent(MainActivity.this, LookImageActivity.class);

        getData("风景", 1, false);

        refreshRecyclerView.refresh(new Action() {
            @Override
            public void onAction() {
                getData(searchWord, 1, true);
            }
        });

        refreshRecyclerView.loadMore(new Action() {
            @Override
            public void onAction() {
                getData(searchWord, pager, false);
            }
        });

    }


    public void getData(final String searchWord, final int pagerNum, final boolean isRefresh) {
        ImageModel.getImageForNet().getImagesInfo(searchWord, pagerNum * 20).enqueue(new Callback<ImageRespond>() {
            @Override
            public void onResponse(Response<ImageRespond> response) {
                if(isRefresh){
                    data.clear();
                    picUrlData.clear();
                    adapter.clear();
                }
                Image[] images = response.body().getItems();
                for (Image image : images) {
                    data.add(image.getThumbUrl());
                    picUrlData.add(image.getPic_url());
                }
                adapter.addAll(data);

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem mSearchMenuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("搜索");
        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                APIService service = ImageModel.getImageForNet();
                service.getImagesInfo(query, pager++).enqueue(new retrofit2.Callback<ImageRespond>() {
                    @Override
                    public void onResponse(retrofit2.Response<ImageRespond> response) {
                        if (response.isSuccess()) {

                            data.clear();
                            picUrlData.clear();

                            Image[] images = response.body().getItems();
                            for (Image image : images) {
                                data.add(image.getThumbUrl());
                                picUrlData.add(image.getPic_url());
                            }
                            adapter.clear();
                            adapter.addAll(data);
                            intent.putExtra(LookImageActivity.IMAGES_DATA_LIST, (Serializable) picUrlData);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }


    /**
     * adapter
     */
    class MyAdapter extends RecyclerAdapter<String> {

        public MyAdapter(Context context) {
            super(context);
        }

        @Override
        public BaseViewHolder<String> onCreateBaseViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return new MyViewHolder(imageView);
        }

        class MyViewHolder extends BaseViewHolder<String> {

            public MyViewHolder(View itemView) {
                super(itemView);

            }

            @Override
            public void setData(final String object) {
                super.setData(object);
                Glide.with(MainActivity.this)
                        .load(object)
                        .error(R.drawable.pc_load)
                        .placeholder(R.drawable.pc_load)
                        .into((ImageView) itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        intent.putExtra(LookImageActivity.IMAGE_NUM, data.indexOf(object));
                        startActivity(intent);
                    }
                });
            }
        }

    }
}
