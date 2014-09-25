package com.thoughtworks.workshop.book;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by wxie on 9/22/14.
 */
public class BookListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "BookListFragment";

    private static final String DATA_URL = "https://api.douban.com/v2/book/search?tag=%s&count=%d&start=%d";
    private static final String DATA_TAG = Uri.encode("编程");
    private static final int DATA_PER_PAGE = 20;
    private static final int DATA_INITIAL_START = 0;

    private AbsListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View loadingView;

    private boolean isLoading;
    private boolean hasMoreItems;
    private MyArrayAdapter adapter;
    private LoadDataTask loadDataTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        listView = (AbsListView) view.findViewById(android.R.id.list);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {/* do nothing */}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0) {
                    int lastVisibleItem = firstVisibleItem + visibleItemCount;
                    if (!isLoading && hasMoreItems && (lastVisibleItem == totalItemCount)) {
                        doLoadMoreData();
                    }
                }
            }
        });
        adapter = new MyArrayAdapter(getActivity());
        listView.setAdapter(adapter);

        loadingView = view.findViewById(R.id.view_loading_more);
        hideLoadingMore();

        if (savedInstanceState == null) {
            doRefreshData();
        } else {
            int firstVisiblePosition = savedInstanceState.getInt("firstVisiblePosition");
            ArrayList<Book> books = savedInstanceState.getParcelableArrayList("data");
            hasMoreItems = savedInstanceState.getBoolean("hasMore");

            adapter.addAll(books);
            listView.smoothScrollToPosition(firstVisiblePosition);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("firstVisiblePosition", listView.getFirstVisiblePosition());
        outState.putParcelableArrayList("data", adapter.getAll());
        outState.putBoolean("hasMore", hasMoreItems);
    }

    private void doLoadMoreData() {
        new LoadDataTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadingMore();
                isLoading = true;
            }

            @Override
            protected void onPostExecute(Data data) {
                super.onPostExecute(data);
                isLoading = false;
                hasMoreItems = data.getTotal() - (data.getStart() + data.getCount()) > 0;
                hideLoadingMore();
                adapter.addAll(data.getBookArray());
            }
        }.execute(getDataUrl(listView.getCount()));

    }

    private void showLoadingMore() {
        loadingView.setVisibility(VISIBLE);
    }

    private void hideLoadingMore() {
        loadingView.setVisibility(GONE);
    }

    private void doRefreshData() {
        loadDataTask = new LoadDataTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                isLoading = true;
                if (!swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            protected void onPostExecute(Data data) {
                super.onPostExecute(data);
                isLoading = false;
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                hasMoreItems = data.getTotal() - (data.getStart() + data.getCount()) > 0;
                adapter.clear();
                adapter.addAll(data.getBookArray());
            }
        };
        loadDataTask.execute(getDataUrl(DATA_INITIAL_START));
    }

    private String getDataUrl(int start) {
        return String.format(DATA_URL, DATA_TAG, DATA_PER_PAGE, start);
    }

    @Override
    public void onRefresh() {
        doRefreshData();
    }

    static class MyArrayAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<Book> data;

        public MyArrayAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            data = new ArrayList<Book>();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Book getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.list_item_book, parent, false);

                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.information = (TextView) convertView.findViewById(R.id.information);
                holder.summary = (TextView) convertView.findViewById(R.id.summary);
                holder.image = (ImageView) convertView.findViewById(R.id.thumbnail);
                holder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating);
                holder.ratingVal = (TextView) convertView.findViewById(R.id.ratingValue);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Book data = getItem(position);

            holder.title.setText(data.getTitle());
            holder.summary.setText(data.getSummary());
            holder.information.setText(data.getInformation());
            holder.ratingBar.setRating((float) (data.getRating() / 2));
            holder.ratingVal.setText(String.valueOf(data.getRating()));
            Picasso.with(parent.getContext()).load(data.getImage()).into(holder.image);
            return convertView;
        }

        public void addAll(List<Book> books) {
            data.addAll(books);
            notifyDataSetChanged();
        }

        public void clear() {
            data.clear();
            notifyDataSetChanged();
        }

        public ArrayList<Book> getAll() {
            return data;
        }

        static class ViewHolder {
            TextView title;
            TextView information;
            TextView summary;
            ImageView image;
            RatingBar ratingBar;
            TextView ratingVal;
        }
    }
}
