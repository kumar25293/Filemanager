package com.demo.samplefileexplorer.daggerinjection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.samplefileexplorer.daggerinjection.ViewModelKey
import com.demo.samplefileexplorer.viewmodel.FileListViewmodel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelmodule {

    @Binds
    @IntoMap
    @ViewModelKey(FileListViewmodel::class)
    internal abstract fun bindOverviewViewModel(viewModel: FileListViewmodel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}