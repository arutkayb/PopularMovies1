package nd.centertableinc.popularmovies1.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import nd.centertableinc.popularmovies1.Adapter.MovieOverviewAdapter;
import nd.centertableinc.popularmovies1.Data.Utils.JsonUtil;
import nd.centertableinc.popularmovies1.Interfaces.AsyncDataListener;
import nd.centertableinc.popularmovies1.Interfaces.RecyclerViewContainer;
import nd.centertableinc.popularmovies1.Data.RecyclerViewItems.MovieItem;
import nd.centertableinc.popularmovies1.Data.MovieDb;
import nd.centertableinc.popularmovies1.Data.Utils.MovieItemUtil;
import nd.centertableinc.popularmovies1.R;

public class MovieOverviewActivity extends AppCompatActivity implements RecyclerViewContainer,AsyncDataListener<String> {
    public List<MovieItem> movieItems;
    private MovieDb movieDb;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieItems = new ArrayList<>();

        recyclerView = findViewById(R.id.movie_overview_recycler_view);

        setRecyclerViewLayoutManager(getResources().getConfiguration());

        movieDb = new MovieDb(this, this, getResources().getString(R.string.api_key));

        requestTheMostPopularMovies(1);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRecyclerViewLayoutManager(newConfig);
    }

    private void setRecyclerViewLayoutManager(Configuration config)
    {
        // Checks the orientation of the screen
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
    }

    private void createMovieCards(JSONObject movieDbJson)
    {
        if(movieItems == null)
            movieItems = new ArrayList<>();

        List<MovieItem> tempMovieItems = MovieItemUtil.getMovieItemsFromMovieDbJson(movieDbJson);

        for(int i = 0; i<tempMovieItems.size(); i++)
            movieItems.add(tempMovieItems.get(i));

        MovieOverviewAdapter movieOverviewAdapter = new MovieOverviewAdapter(this, this, movieItems);

        recyclerView.setAdapter(movieOverviewAdapter);

        recyclerView.scrollToPosition(movieItems.size() - tempMovieItems.size() - 1);
    }

    @Override
    public void onCustomClickListener(int itemPosition)
    {
        if(movieItems != null)
            MovieItemUtil.setSelectedMovieItem(movieItems.get(itemPosition));

        Intent intent = new Intent(this, MovieDetailsActivity.class);
        startActivity(intent);
    }

    public void requestTheMostPopularMovies(int page)
    {
        movieDb.requestForTheMostPopularMovies(page);
    }

    public void requestTheHighestRatedMovies(int page)
    {
        movieDb.requestForTheHighestRatedMovies(page);
    }

    @Override
    public void onDataLoad(String result){
        JSONObject res = JsonUtil.createJsonObjFromJsonString(result);

        if(res != null)
            createMovieCards(res);
    }


    @Override
    public void lastItemHitListener(int itemPosition)
    {
        requestTheMostPopularMovies(movieDb.getCurrentPage() + 1);
    }
}
