package com.thuypham.ptithcm.learningrxjava.di

import com.thuypham.ptithcm.learningrxjava.viewmodel.MainViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel { MainViewModel() }
}
