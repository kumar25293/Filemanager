package com.demo.samplefileexplorer.ui

import android.Manifest
import android.R.attr
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.samplefileexplorer.App
import com.demo.samplefileexplorer.BuildConfig
import com.demo.samplefileexplorer.BuildConfig.APPLICATION_ID
import com.demo.samplefileexplorer.R
import com.demo.samplefileexplorer.adapters.FilesAdapter
import com.demo.samplefileexplorer.dialogs.Dialog
import com.demo.samplefileexplorer.helpers.Files
import com.demo.samplefileexplorer.helpers.Permissions
import com.demo.samplefileexplorer.models.FileModel
import com.demo.samplefileexplorer.databinding.FragmentFilesBinding
import com.demo.samplefileexplorer.viewmodel.FileListViewmodel
import java.io.File
import javax.inject.Inject
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import android.R.attr.mimeType

import android.content.ContentResolver










class FilesFragment : Fragment() {
    private var _binding: FragmentFilesBinding? = null
    private lateinit var callback: OnBackPressedCallback
    private val binding get() = _binding!!
    private var path = ""
    private  var stoargepath =""
    private var filesrcpath = ""
    private var filedespath = ""
    private var curORcopytype =""
//    private val array = arrayListOf<FileModel>()
     var fileAdapter: FilesAdapter?=null
    private lateinit var launcher: ActivityResultLauncher<String>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    lateinit var viewModel: FileListViewmodel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Handling OnBack Button Pressed Situation

        callback=
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    handlebackPress()
//                    val newPath = path.subSequence(0, path.lastIndexOf("/")).toString()
//                     val tempath = getPath().subSequence(0, path.lastIndexOf("/")).toString()
//                    if(!newPath.equals(tempath,true)) {
//                        path = newPath
//                        fileAdapter?.directoryOnClick(Files.getFiles(requireContext(), newPath))
//                    }
//                    else{
//                        requireActivity().finish()
//                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        App.appComponent.inject(this)

        viewModel = ViewModelProvider(this, viewModelFactory).get(FileListViewmodel::class.java)

        _binding = FragmentFilesBinding.inflate(inflater, container, false)
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true);
        launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                setUpRecyclerView()
            }

        }
        // Checking For Permission
        if(Permissions.isPermissionGranted(requireContext())){
            setUpRecyclerView()

        }else{
            // Asking For Manage_External_Storage Permission
            if (Permissions.isAndroidSdkGreaterThan10()){
                try {
                    val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                    startActivity(intent)
                } catch (ex: Exception) {
                    val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                    startActivity(intent)
                }
            }else{
                launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            }

        }

        binding.backarrow.setOnClickListener{
            handlebackPress()
        //            val newPath = path.subSequence(0, path.lastIndexOf("/")).toString()
//            path = newPath
//            fileAdapter?.directoryOnClick(Files.getFiles(requireContext(), newPath))
        }

        binding.floatingActionButton.setOnClickListener {
            createFileDialog(requireContext(),path)
        }

        binding.filepaste.setOnClickListener{
            val f = File(filesrcpath)
            val filename: String = f.name
             filedespath = path.plus("/").plus(filename)
            if(Files.copyFile(filesrcpath,filedespath,requireContext(),curORcopytype))
            {
                ResetAdapterData(this.path)
                binding.filepaste.visibility=View.GONE
                binding.cancel.visibility=View.GONE
                filesrcpath =""
                filedespath =""
                curORcopytype =""
            }
        }

        binding.cancel.setOnClickListener{
            binding.filepaste.visibility=View.GONE
            binding.cancel.visibility=View.GONE
            filesrcpath =""
            filedespath =""
            curORcopytype =""
        }

        fileAdapter?.onItemClick = { path, position,type ->


                if(type=="File/Folder") {
                    this.path = path
                    binding.backarrow.visibility =View.VISIBLE
                }
                else{
                    if(type.equals("Rename",true)) {
                        createFileRenameDialog(requireContext(),path)
                    }
                    else if(type.equals("Copy",true)
                        ||type.equals("Cut",true)){
                        binding.filepaste.visibility=View.VISIBLE
                        binding.cancel.visibility=View.VISIBLE
                        binding.backarrow.visibility=View.GONE
                        ResetAdapterData(getPath())

                        filesrcpath =path
                        curORcopytype =type
                    }
                    else if(type.equals("Share",true)){
                        shareFiles(path)
                    }
                    else if(type.equals("Delete",true)){
                        Files.deletefile(path,requireContext())
                        ResetAdapterData(this.path)
                    }
                    else{
                        ResetAdapterData(getPath())
                    }
                }
            }
        return binding.root
    }
    // Getting Path
    private fun getPath(): String {
        val storageManager = requireContext().getSystemService(StorageManager::class.java)
        path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            storageManager.primaryStorageVolume.directory?.path?:""

        } else {
            Environment.getExternalStorageDirectory().path.toString()

        }
        Toast.makeText(requireContext(), path, Toast.LENGTH_SHORT).show()
        if(stoargepath.isNullOrEmpty())
            stoargepath =path
        return path
    }

    private fun handlebackPress(){
        val newPath = path.subSequence(0, path.lastIndexOf("/")).toString()
        var tempath = getPath().subSequence(0, path.lastIndexOf("/")).toString()

        if(!newPath.equals(tempath,true)) {
            path = newPath
            fileAdapter?.directoryOnClick(Files.getFiles(requireContext(), newPath))
            if(newPath.equals(stoargepath,true))
                binding.backarrow.visibility =View.GONE
         }
        else{
            requireActivity().finish()
        }
    }

    private fun getFileList( path:String):ArrayList<FileModel>{
        val array1 = viewModel.getFiles(requireContext(), path/*getPath()*/)
          return  array1
    }

    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val filelist = getFileList(getPath())
        println("Current path $path")
        fileAdapter = FilesAdapter(requireContext(),filelist /*array*/ )
        binding.recyclerview.apply {
            this.adapter = fileAdapter
            this.layoutManager = layoutManager
        }

        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 ||dy<0 &&  binding.floatingActionButton.isShown())
                {
                    binding.floatingActionButton.hide();
                }
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    binding.floatingActionButton.show();
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private fun ResetAdapterData(path: String){
        val filelist = getFileList(path)
        println("Refresh array $filelist")
        fileAdapter?.submitData(filelist)
    }

    fun createFileDialog(context: Context, path: String) {
        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.create_new_file_dialog, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancelBtn)
        val createBtn = dialogView.findViewById<Button>(R.id.createBtn)
        val fileRadioBtn = dialogView.findViewById<RadioButton>(R.id.fileRadioBtn)
        val folderRadioBtn = dialogView.findViewById<RadioButton>(R.id.folderRadioBtn)
        fileRadioBtn.isChecked = true
        val editText = dialogView.findViewById<EditText>(R.id.editTextTextFileName)
        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
        createBtn.setOnClickListener {
            if (folderRadioBtn.isChecked) {
                val file = File(path, editText.text.toString())
                if (file.exists()) {
                    editText.error = "Folder Already Exists!"

                } else {
                    if (Files.createFolder(path, editText.text.toString(), context)
                    ) {
                        println("Folder create path $path")
                         ResetAdapterData(path)
                        Toast.makeText(context, "${editText.text} Folder Created", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                    } else {
                        Toast.makeText(context, "${editText.text} Folder Not Created", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (fileRadioBtn.isChecked) {
                val file = File(path, editText.text.toString())
                if (file.exists()) {
                    editText.error = "File Already Exists!"
                } else {
                    if (Files.createFile(path, editText.text.toString(), context)
                    ) {
                        println("File create path $path")
                        ResetAdapterData(path)
                        Toast.makeText(context, "${editText.text} File Created", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                    } else {
                        Toast.makeText(context, "File Not Created", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Please Select A Type", Toast.LENGTH_SHORT).show()
            }

        }
        fileRadioBtn.setOnClickListener {
            folderRadioBtn.isChecked = false
        }
        folderRadioBtn.setOnClickListener {
            fileRadioBtn.isChecked = false

        }
    }

    fun createFileRenameDialog(context: Context, path: String) {
        var srcfile = File(path)
        val filename: String = srcfile.nameWithoutExtension
        val builder = AlertDialog.Builder(context)
        val dialogView = layoutInflater.inflate(R.layout.rename_dialog, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val cancelBtn = dialogView.findViewById<Button>(R.id.btncancel)
        val renamebtn = dialogView.findViewById<Button>(R.id.renameBtn)

        val editText = dialogView.findViewById<EditText>(R.id.et_renamee_fileName)
        editText.setText(filename)

        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()

        renamebtn.setOnClickListener {
            val filepath:String = srcfile.parent.plus("/").plus(editText.text.toString()).plus(".").plus(srcfile.extension)
            val dest = File(filepath)
            if (srcfile.renameTo(dest)) {
                println("Rename file path ${srcfile.parent}")
                ResetAdapterData(srcfile.parent)
                Toast.makeText(context,
                    "${editText.text} File is Renamed",
                    Toast.LENGTH_SHORT
                )
                    .show()
                alertDialog.dismiss()
            } else {
                Toast.makeText(context,
                    "${editText.text} File rename failed",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }


    private fun shareFiles(path:String){
        val sharefile = File(path)
        val uri = FileProvider.getUriForFile(context!!, "com.demo.samplefileexplorer.providers.MyFileProvider", sharefile)
        val cr =requireContext().contentResolver
        val mimeType = cr.getType(uri)
        println("Share file path == $path")
        println("Share file uro == $uri")
        println("Share file mime == $mimeType")
        val intent = ShareCompat.IntentBuilder(requireActivity())
            .setType(mimeType)
            .setStream(uri)
            .setChooserTitle("Choose bar")
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//
        startActivity(intent)
    }

}


