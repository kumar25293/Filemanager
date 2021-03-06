package com.demo.samplefileexplorer.dialogs

import android.R.attr
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.demo.samplefileexplorer.R
import com.demo.samplefileexplorer.helpers.Files
import com.demo.samplefileexplorer.helpers.Utils
import java.io.File
import android.R.attr.path




class Dialog {
    companion object {

        fun createFileDialog(fragment: Fragment, path: String): Boolean {
            var isFileOrFolderCreated = false
            val builder = AlertDialog.Builder(fragment.requireContext())
            val dialogView = fragment.layoutInflater.inflate(R.layout.create_new_file_dialog, null)
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
                        if (Files.createFolder(
                                path,
                                editText.text.toString(),
                                fragment.requireContext()
                            )
                        ) {
                            isFileOrFolderCreated = true
                            Toast.makeText(
                                fragment.requireContext(),
                                "${editText.text} Folder Created",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            alertDialog.dismiss()
                        } else {
                            Toast.makeText(
                                fragment.requireContext(),
                                "${editText.text} Folder Not Created",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                } else if (fileRadioBtn.isChecked) {
                    val file = File(path, editText.text.toString())
                    if (file.exists()) {
                        editText.error = "File Already Exists!"
                    } else {
                        if (Files.createFile(
                                path,
                                editText.text.toString(),
                                fragment.requireContext()
                            )
                        ) {
                            isFileOrFolderCreated = true
                            Toast.makeText(
                                fragment.requireContext(),
                                "${editText.text} File Created",
                                Toast.LENGTH_SHORT
                            ).show()

                            alertDialog.dismiss()
                        } else {
                            Toast.makeText(
                                fragment.requireContext(),
                                "File Not Created",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }

                } else {
                    Toast.makeText(
                        fragment.requireContext(),
                        "Please Select A Type",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            fileRadioBtn.setOnClickListener {
                folderRadioBtn.isChecked = false
            }
            folderRadioBtn.setOnClickListener {
                fileRadioBtn.isChecked = false

            }
            return isFileOrFolderCreated

        }


        fun createFileRenameDialog(fragment: Fragment, path: String): Boolean {
            var srcfile = File(path)
            val filename: String = srcfile.nameWithoutExtension
            var isFileOrFolderRenamed = false
            val builder = AlertDialog.Builder(fragment.requireContext())
            val dialogView = fragment.layoutInflater.inflate(R.layout.rename_dialog, null)
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
                    isFileOrFolderRenamed = true
                    Toast.makeText(
                        fragment.requireContext(),
                        "${editText.text} File is Renamed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    alertDialog.dismiss()
                } else {
                    Toast.makeText(
                        fragment.requireContext(),
                        "${editText.text} File rename failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            return isFileOrFolderRenamed

        }

    }

}