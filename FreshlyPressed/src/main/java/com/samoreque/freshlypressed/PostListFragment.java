package com.samoreque.freshlypressed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.samoreque.freshlypressed.api.PostsRequestException;
import com.samoreque.freshlypressed.api.WordPressService;
import com.samoreque.freshlypressed.components.LoadMoreRecyclerView;
import com.samoreque.freshlypressed.exceptions.EmptyPostsException;
import com.samoreque.freshlypressed.exceptions.LoadMoreException;
import com.samoreque.freshlypressed.extensions.ViewExtensionsKt;
import com.samoreque.freshlypressed.models.Post;
import com.samoreque.freshlypressed.respository.NetworkRepository;
import com.samoreque.freshlypressed.ui.PostsAdapter;
import com.samoreque.freshlypressed.ui.PostsViewModel;

import kotlinx.coroutines.Dispatchers;

public class PostListFragment extends Fragment {
    public static final String TAG = PostListFragment.class.getName();
    public static final String LAYOUT_MANAGER_STATE_KEY = "layoutManagerStateKey";
    final NetworkRepository repository = new NetworkRepository(WordPressService.create(), Dispatchers.getIO());

    private PostsViewModel viewModel;
    private LoadMoreRecyclerView postRecyclerView;
    private ProgressBar loadingProgressBar;
    private PostsAdapter postsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button errorButton;
    private TextView errorMessage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, PostsViewModel.getFactory().invoke(repository))
                .get(PostsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.posts_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUI(view);
        viewModel.getPostList().observe(this.getViewLifecycleOwner(), this::updatePosts);
        viewModel.getPostUpdated().observe(this.getViewLifecycleOwner(), this::updatePost);
        viewModel.getError().observe(this.getViewLifecycleOwner(), this::handleError);
        viewModel.getSpinner().observe(this.getViewLifecycleOwner(), this::enableSpinner);
        viewModel.getRefreshSpinner().observe(this.getViewLifecycleOwner(), this::enableRefreshSpinner);
    }

    private void setUI(View view) {

        errorButton = view.findViewById(R.id.retryButton);
        errorMessage = view.findViewById(R.id.errorMessage);

        linearLayoutManager = new LinearLayoutManager(this.getContext());
        postRecyclerView = view.findViewById(R.id.postsRecyclerview);
        postRecyclerView.setLayoutManager(linearLayoutManager);
        postRecyclerView.onLoadMoreListener(() -> viewModel.onLoadMore());

        postsAdapter = new PostsAdapter(GlideApp.with(this), Collections.emptyList(),
                (position, post) -> viewModel.loadSubscribersCount(position, post.getAuthor().getUrl()));

        postRecyclerView.setAdapter(postsAdapter);

        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);

        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refreshPost());
        Toolbar toolBar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) activity.setSupportActionBar(toolBar);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(LAYOUT_MANAGER_STATE_KEY)) {
            linearLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE_KEY));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(LAYOUT_MANAGER_STATE_KEY, linearLayoutManager.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    private void enableSpinner(Boolean enabled) {
        if (enabled) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.INVISIBLE);
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    private void enableRefreshSpinner(Boolean enabled) {
        if (!enabled) {
            swipeRefreshLayout.setEnabled(true);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel.getPostsCache() == null) {
            loadPosts();
        }
    }

    protected void updatePost(Integer position) {
        if (postsAdapter != null) postsAdapter.notifyItemChanged(position);
    }

    protected void updatePosts(List<Post> posts) {
        postRecyclerView.loadMoreFinished();
        postsAdapter.setPosts(posts);
        postsAdapter.notifyDataSetChanged();
    }

    public void loadPosts() {
        viewModel.loadPosts(false);
    }

    private void handlePostError(Throwable throwable, String userMessage) {
        enableErrorView(true);
        errorMessage.setText(userMessage);
        errorButton.setOnClickListener(v -> {
            enableErrorView(false);
            loadPosts();
        });
    }

    private void handleError(Throwable throwable) {
        Log.e(TAG, getString(R.string.posts_error), throwable);
        if (throwable instanceof PostsRequestException) {
            handlePostError(throwable, getString(R.string.posts_error_retry));
        } else if (throwable instanceof EmptyPostsException) {
            handlePostError(throwable, getString(R.string.posts_not_found));
        } else if (throwable instanceof LoadMoreException) {
            postRecyclerView.loadMoreFinished();
        }
        viewModel.errorHandled();
    }

    private void enableErrorView(boolean enabled) {
        if (enabled) {
            ViewExtensionsKt.visible(errorButton);
            ViewExtensionsKt.visible(errorMessage);
        } else {
            ViewExtensionsKt.gone(errorButton);
            ViewExtensionsKt.gone(errorMessage);
        }
    }
}
