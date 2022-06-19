package com.thuypham.ptithcm.learningrxjava

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thuypham.ptithcm.learningrxjava.databinding.ActivityMainBinding
import com.thuypham.ptithcm.learningrxjava.model.ResponseHandler
import com.thuypham.ptithcm.learningrxjava.ui.MovieAdapter
import com.thuypham.ptithcm.learningrxjava.viewmodel.MainViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    private lateinit var binding: ActivityMainBinding
    private val movieAdapter: MovieAdapter by lazy {
        MovieAdapter {}

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        getData()
//        viewModel.getGenresData()
        viewModel.getGenresData1()
        setupDataObserver()
        setupRecyclerView()
    }

    private fun getData() {
        viewModel.getData()
    }

    private var isLoadingSuccess = false
    private fun setupRecyclerView() {
        binding.apply {
            rvMovie.adapter = movieAdapter
            rvMovie.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    //Nếu item cuối cùng của layout = với giá trị cuối của recycleView thì ta gọi hàm LoadMore
                    if (isLoadingSuccess && linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == movieAdapter.currentList.size - 5) {
                        isLoadingSuccess = false
                        //bottom of list!
                        getData()
                        progressBar.isVisible = true
                    }
                }
            })
        }
    }

    private fun setupDataObserver() {
        viewModel.movieResponse.observe(this) {
            Log.d("thuuy", it.toString())
            isLoadingSuccess = true
            when (it) {
                is ResponseHandler.Success -> {
                    movieAdapter.submitList(it.data)
                    movieAdapter.notifyDataSetChanged()
                    binding.progressBar.isVisible = false
                }

                is ResponseHandler.Loading -> {
                    binding.progressBar.isVisible = true
                }
                else -> {
                    binding.progressBar.isVisible = false

                }
            }
        }
    }
}