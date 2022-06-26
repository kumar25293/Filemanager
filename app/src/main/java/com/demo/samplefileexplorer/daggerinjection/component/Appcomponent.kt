package com.demo.samplefileexplorer.daggerinjection.component

import com.demo.samplefileexplorer.daggerinjection.viewmodel.ViewModelmodule
import com.demo.samplefileexplorer.ui.FilesFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelmodule::class])
interface AppComponent{
    fun inject(fileFragment: FilesFragment)

}