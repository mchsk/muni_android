package cz.muni.pv239.marek.cv5;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cz.muni.pv239.marek.cv5.api.GitHubService;
import cz.muni.pv239.marek.cv5.model.User;
import cz.muni.pv239.marek.cv5.recyclerview.DividerItemDecoration;
import cz.muni.pv239.marek.cv5.recyclerview.RecyclerTouchListener;
import cz.muni.pv239.marek.cv5.recyclerview.WatchersAdapter;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import timber.log.Timber;

public class RecyclerViewActivity extends AppCompatActivity {

    private final List<User> watcherList = new ArrayList<>();

    @Inject
    WatchersAdapter mAdapter;

    @Inject
    GitHubService mGitHubService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        ((Cv5App) getApplication()).getAppComponent().inject(this);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter.setWatcherList(watcherList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                User user = watcherList.get(position);
                Toast.makeText(getApplicationContext(), "You clicked + " + user.getLogin(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        loadWatchers("openwrt", "openwrt");
    }


    private void loadWatchers(String username, String repositoryName) {
        Observable<Response<List<User>>> watchersObservable = mGitHubService.getWatcherList(username, repositoryName);
        watchersObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(createWatchersObserver());
    }

    private Observer createWatchersObserver() {
        return new DisposableObserver<Response<List<User>>>() {

            @Override
            public void onNext(Response<List<User>> listResponse) {
                watcherList.addAll(listResponse.body());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
            }
        };
    }


}
