package com.demo.samplefileexplorer.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.demo.samplefileexplorer.helpers.Files
import com.demo.samplefileexplorer.models.FileModel
import kotlinx.coroutines.delay
import java.io.File
import javax.inject.Inject

class FileListViewmodel @Inject constructor() : ViewModel() {
     var filedata: LiveData<List<FileModel>>? = null


      fun getFiles(context: Context, path:String?): ArrayList<FileModel> {

          return  Files.getFiles(context,path)

      }
}