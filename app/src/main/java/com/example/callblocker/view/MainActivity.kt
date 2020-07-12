package com.example.callblocker.view

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.callblocker.R
import com.example.callblocker.model.BlockedNumber
import com.example.callblocker.viewmodel.CallBlockerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val REQUEST_CONTACT_INTENT_ID = 200
    private val REQUEST_PERMISSION_READ_PHONE_STATE = 201

    val viewModel: CallBlockerViewModel by viewModels()

    lateinit var adapter: BlackListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab_add.setOnClickListener {
            showNumberBlocker()
        }

        viewModel.getBlackList()?.observe(this, Observer {
            showBlackList(it)
        })

        //Get permissions needed for listening to incoming calls and blocking calls
        requestRequiredPermissions()

    }

    private fun showBlackList(list: List<BlockedNumber>?) {
        adapter = BlackListAdapter(list) { blockedNumber ->
            //On delete click action
            viewModel.unblockNumber(blockedNumber)
        }
        val layoutManager = LinearLayoutManager(this)
        rv_blacklist.layoutManager = layoutManager
        rv_blacklist.adapter = adapter
    }

    private fun showNumberBlocker() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Block number")
        builder.setItems(
            arrayOf(getString(R.string.lbl_number), getString(R.string.lbl_contacts)),
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    0 -> {  //Number
                        //Show an input dialog
                        getNumberInput()
                    }
                    1 -> { //Contacts
                        //direct to contacts
                        getContactInput()
                    }
                }
            })
        builder.show()
    }

    private fun getNumberInput() {
        NumberInputDialog.show(this, rv_blacklist) { blockNumber(it) }
    }

    private fun getContactInput() {
        val i = Intent(Intent.ACTION_PICK)
        i.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(i, REQUEST_CONTACT_INTENT_ID)
    }

    private fun blockNumber(number: String, name: String? = null) {
        viewModel.blockNumber(number, name)
    }

    private fun requestRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED
                || checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_DENIED
            ) {
                val permissions = arrayOf<String>(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.ANSWER_PHONE_CALLS
                )
                requestPermissions(permissions, REQUEST_PERMISSION_READ_PHONE_STATE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CONTACT_INTENT_ID && resultCode == RESULT_OK) { // Got a contact
            //Read Uri path
            val contactUri = data?.data
            contactUri?.let {
                //Fetch content from Uri
                val cursor = contentResolver.query(
                    contactUri, arrayOf(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    ),
                    null, null, null
                )

                // If the cursor returned is valid, get the phone number
                if (cursor != null && cursor.moveToFirst()) {
                    //Read the first contact
                    val numberIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val number = cursor.getString(numberIndex)

                    val nameIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val name = cursor.getString(nameIndex)

                    //Add it to blocked repo
                    blockNumber(number, name)
                }
                cursor?.close()
            }
        } else if (requestCode == REQUEST_PERMISSION_READ_PHONE_STATE && resultCode == RESULT_OK) {
            //Nothing to be done
        }
    }
}