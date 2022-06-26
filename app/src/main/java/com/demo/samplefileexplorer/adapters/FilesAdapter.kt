package com.demo.samplefileexplorer.adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.demo.samplefileexplorer.R
import com.demo.samplefileexplorer.databinding.ItemsBinding
import com.demo.samplefileexplorer.extensions.checkFileSize
import com.demo.samplefileexplorer.helpers.Files
import com.demo.samplefileexplorer.models.FileModel
import com.demo.samplefileexplorer.helpers.Utils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FilesAdapter(private var context: Context, var arrayList: ArrayList<FileModel>):RecyclerView.Adapter<FilesAdapter.MyViewHolder>() {
    var onItemClick:((path:String, position:Int,type:String) -> Unit)? = null

    inner class MyViewHolder(binding: ItemsBinding):RecyclerView.ViewHolder(binding.root){
        val fileNameTextView = binding.textViewFileName
        val imageView = binding.imageView
        val fileExtensionNameTextView = binding.fileExtensionNameTextView
        val lastModifiedTextView = binding.lastModifiedTextView
        val dotimg = binding.imageView1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =ItemsBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return MyViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val file = arrayList[position]
        holder.fileNameTextView.text = file.name
        val simpleDataFormat =  SimpleDateFormat.getDateInstance().format(Date(file.lastModified))
        holder.lastModifiedTextView.text = simpleDataFormat


        if (file.isDirectory){
//            if (position == 0){
//                holder.fileExtensionNameTextView.text= ""
//                holder.lastModifiedTextView.text= "Previous Folder"
//                holder.imageView.setImageDrawable(ActivityCompat.getDrawable(context,
//                    R.drawable.ic_back_folder
//                ))
//            }else{
                holder.imageView.setImageDrawable(ActivityCompat.getDrawable(context,
                    R.drawable.ic_folder
                ))
                holder.fileExtensionNameTextView.visibility = View.GONE
                holder.dotimg.visibility = View.GONE
//                holder.fileExtensionNameTextView.text = context.getString(R.string.folder)
//            }

        }else{
            holder.fileExtensionNameTextView.text = File(file.absolutePath).checkFileSize().plus(",")
//            holder.fileExtensionNameTextView.text = File(file.absolutePath).checkFileSize().plus(",")
            holder.imageView.setImageDrawable(Utils.getFileDrawable(File(file.absolutePath), context))
            holder.dotimg.visibility = View.VISIBLE

        }



        holder.itemView.setOnClickListener {
            if (file.isDirectory){
                val path = file.absolutePath
                onItemClick?.invoke(path,position,"File/Folder")
                directoryOnClick(Files.getFiles(context, path))

            }else{
                try {
                    val apkUri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".providers.MyFileProvider", File(file.absolutePath))
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(apkUri, null)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    }
                    val chooser = Intent.createChooser(intent, "Choose a App")

                    context.startActivity(chooser)
                }catch (e:ActivityNotFoundException){
                    Log.d("hello", e.printStackTrace().toString())
                }


            }
        }

        holder.itemView.setOnLongClickListener {
           if(!file.isDirectory)
            println("recylerview long clicked ")
            true
        }
        holder.dotimg.setOnClickListener{
            val path = file.absolutePath
            showPopup(it,path,position)
        }
    }
    fun directoryOnClick(arrayList: ArrayList<FileModel>){
        this.arrayList = arrayList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private fun showPopup(view: View, path: String, position: Int) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.header_menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.header1 -> {
                    onItemClick?.invoke(path,position,item.title.toString())
                }
                R.id.header2,  R.id.header3 -> {
                    onItemClick?.invoke(path,position,item.title.toString())
                }
                R.id.header4 -> {
                    onItemClick?.invoke(path,position,item.title.toString())
                }
                R.id.header5 -> {
                    onItemClick?.invoke(path,position,item.title.toString())
                }
            }

            true
        })

        popup.show()
    }

    fun submitData(arrayList: ArrayList<FileModel>){
        this.arrayList.clear()
        this.arrayList = arrayList
        notifyDataSetChanged()
    }

}