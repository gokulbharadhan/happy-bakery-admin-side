package com.example.bakery.fragments.shopping

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bakery.data.Product
import com.example.bakery.util.showBottomNavigationView
import com.example.products.R
import com.example.products.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID


class HomeFragment: Fragment(R.layout.fragment_home), AdapterView.OnItemSelectedListener  {
    private lateinit var binding: FragmentHomeBinding
    var details="KG"
    var specialization="None"
    var selectedImages = mutableListOf<Uri>()
    val firestore = Firebase.firestore
    var selection = "Mixture"
    private val storage = Firebase.storage.reference
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding=FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Terminate the app when the back button is pressed
                requireActivity().finishAffinity()
            }
        })
        var items = arrayOf<String>("Bread", "Cakes", "Chips", "Chocolates", "Mixture", "Snacks", "Sweets", "IceCream","CoolDrinks")
        binding.spinner.onItemSelectedListener = this
        val adapter: ArrayAdapter<CharSequence> =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        val spinnerPosition: Int = adapter.getPosition(selection)
        binding.spinner.setSelection(spinnerPosition)

        //spinner for product specialization
        var spItems = arrayOf<String>("None","Special Products", "Best Deals","Best Products")
        binding.spinnerSpecialization.onItemSelectedListener = this
        val spAdapter: ArrayAdapter<CharSequence> =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spItems)
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSpecialization.adapter = spAdapter
        val spSpinnerPosition: Int = spAdapter.getPosition(specialization)
        binding.spinnerSpecialization.setSelection(spSpinnerPosition)

        //spinner for product details
        var dtItems = arrayOf<String>("KG", "Gram","KG & Gram","Piece","Bundle","Piece & Bundle")
        binding.spinnerDetails.onItemSelectedListener = this
        val dtAdapter: ArrayAdapter<CharSequence> =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dtItems)
        dtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDetails.adapter = dtAdapter
        val dtSpinnerPosition: Int = dtAdapter.getPosition(specialization)
        binding.spinnerDetails.setSelection(dtSpinnerPosition)



        //selecting images
        val selectImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data

                    //Multiple images selected
                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount ?: 0
                        (0 until count).forEach {
                            val imagesUri = intent.clipData?.getItemAt(it)?.uri
                            imagesUri?.let { selectedImages.add(it) }
                        }

                        //One images was selected
                    } else {
                        val imageUri = intent?.data
                        imageUri?.let { selectedImages.add(it) }
                    }
                    updateImages()
                }
            }
        //clicking image button
        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImagesActivityResult.launch(intent)
        }
        binding.buttonSave.setOnClickListener{
                val productValidation = validateInformation()
                if (!productValidation) {
                    Toast.makeText(requireContext(), "Check your inputs", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                saveProducts() {
                    Log.d("test", it.toString())
                }
        }


    }


    //for menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //menu save button click
        if (item.itemId == R.id.saveProduct) {
            val productValidation = validateInformation()
            if (!productValidation) {
                Toast.makeText(requireContext(), "Check your inputs", Toast.LENGTH_SHORT).show()
                return false
            }
            saveProducts() {
                Log.d("test", it.toString())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //validating
    private fun validateInformation(): Boolean {
        if (selectedImages.isEmpty())
            return false
        if (binding.edName.text.toString().trim().isEmpty())
            return false
        if (binding.edPrice.text.toString().trim().isEmpty())
            return false
        return true
    }

    //saving the data into firebase
    private fun saveProducts(state: (Boolean) -> Unit) {
        val price1=binding.edPrice2.text.toString().trim()
        val imagesByteArrays = getImagesByteArrays() //7
        val name = binding.edName.text.toString().trim()
        val images = mutableListOf<String>()
        val productDescription = binding.edDescription.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val offerPercentage = binding.edOfferPercentage.text.toString().trim()

        lifecycleScope.launch {
            showLoading()
            try {
                async {
                    Log.d("test1", "test")
                    imagesByteArrays.forEach {
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imagesStorage = storage.child("products/images/$id")
                            val result = imagesStorage.putBytes(it).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)
                        }
                    }
                }.await()
            } catch (e: java.lang.Exception) {
                hideLoading()
                state(false)
            }

            Log.d("test2", "test")

            val product = Product(
                UUID.randomUUID().toString(),
                name,
                selection,
                specialization,
                price.toFloat(),
                if(price1.isEmpty()) null else price1.toFloat(),
                if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                if (productDescription.isEmpty()) null else productDescription,
                details,
                images
            )
            try {
                firestore.collection("Products").add(product).addOnSuccessListener {
                    state(true)
                    hideLoading()
                }.addOnFailureListener {
                    Log.e("test2", it.message.toString())
                    state(false)
                    hideLoading()
                }
            }catch (e: FirebaseException){
                Log.e("Firebase","${e}")
            }
            binding.edName.setText("")
            binding.edPrice.setText("")
            binding.edDescription.setText("")
            binding.edOfferPercentage.setText("")
            selectedImages.clear()
            binding.edPrice2.setText("")
            updateImages()
        }
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE

    }

    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach {
            val stream = ByteArrayOutputStream()
            val imageBmp = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG, 85, stream)) {
                val imageAsByteArray = stream.toByteArray()
                imagesByteArray.add(imageAsByteArray)
            }
        }
        return imagesByteArray
    }

    private fun getSizesList(sizes: String): List<String>? {
        if (sizes.isEmpty())
            return null
        val sizesList = sizes.split(",").map { it.trim() }
        return sizesList
    }

    //5

    private fun updateImages() {
        binding.tvSelectedImages.setText(selectedImages.size.toString())
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        selection = (binding.spinner.selectedItem as? String).toString()
        Log.e("selected","${selection}")
        specialization=(binding.spinnerSpecialization.selectedItem as? String).toString()
        Log.e("selected","${specialization}")
        details=(binding.spinnerDetails.selectedItem as? String).toString()
        Log.e("selected","${details}")
        checkpricedetails()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
    private fun checkpricedetails(){
        if(details.equals("KG")) {
            binding.edPrice.hint = "price/kg"
            Log.d("if statement","${details}")
            binding.edPrice2.visibility = View.GONE
        }
        else if(details.equals("Gram")) {
            Log.d("if statement","${details}")
            binding.edPrice.hint = "price/gram"
            binding.edPrice2.visibility=View.GONE
        }
        else if(details=="Piece") {
            binding.edPrice.hint = "price/piece"
            binding.edPrice2.visibility=View.GONE
        }
        else if(details.equals("Bundle")) {
            binding.edPrice.hint = "price/Bundle"
            binding.edPrice2.visibility=View.GONE
        }
        else if(details.equals("KG & Gram")){
            binding.edPrice.hint="price/kg"
            binding.edPrice2.visibility=View.VISIBLE
            binding.edPrice2.hint="price/Gram"
        }else if(details.equals("Piece & Bundle")){
            binding.edPrice.hint="price/Piece"
            binding.edPrice2.visibility=View.VISIBLE
            binding.edPrice2.hint="price/Bundle"
        }


    }


}