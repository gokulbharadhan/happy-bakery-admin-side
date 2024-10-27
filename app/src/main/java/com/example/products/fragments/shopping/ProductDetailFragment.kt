package com.example.products.fragments.shopping

import android.R
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bakery.data.Product
import com.example.bakery.util.Resource
import com.example.products.adapter.ViewPager2Images
import com.example.products.databinding.FragmentProductDetailBinding
import com.example.products.viewmodel.CartViewModel
import com.example.products.viewmodel.ProductUpgradeViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

@AndroidEntryPoint
class ProductDetailFragment: Fragment(), AdapterView.OnItemSelectedListener {
    private val args by navArgs<ProductDetailFragmentArgs>()

    private lateinit var binding: FragmentProductDetailBinding
    private val viewPageAdapter by lazy { ViewPager2Images() }
    private val storage = Firebase.storage.reference
    val firestore = Firebase.firestore
    var images1= listOf<String>()
    val viewModel by viewModels<CartViewModel>()
    var productid=""
    var selectedImages = mutableListOf<Uri>()
    var details = ""
    var specialization = ""
    var category = ""
    var price=""
    var price1=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product1 = args.product
        lifecycleScope.launchWhenStarted {
            viewModel.updateSingle.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.progressbar.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        binding.progressbar.visibility=View.GONE
                        findNavController().navigateUp()
                    }
                    is Resource.Error->{
                        binding.progressbar.visibility=View.VISIBLE
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }
        setUpViewPagerRv()
        binding.imageCloseButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            edName.setText(product1.name)
            edDescription.setText(product1.description)
            details = product1.details
            images1=product1.images
            category = product1.category
            productid=product1.id
            price=product1.price.toString()
            if(product1.price1==null)
                price1=""
            else
                price1=product1.price1.toString()
            specialization = product1.specialization
            val spinnerItemList1 = listOf(
                "Bread",
                "Cakes",
                "Chips",
                "Chocolates",
                "Mixture",
                "Snacks",
                "Sweets",
                "IceCream",
                "CoolDrinks"
            ) // Example items
            val spinnerItemList2 =
                listOf("None", "Special Products", "Best Deals", "Best Products") // Example items
            val spinnerItemList3 = listOf(
                "KG",
                "Gram",
                "KG & Gram",
                "Piece",
                "Bundle",
                "Piece & Bundle"
            ) // Example items

// Set adapter for spinner1
            val spinnerAdapter1 =
                ArrayAdapter(requireContext(), R.layout.simple_spinner_item, spinnerItemList1)
            spinnerAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter1
            spinner.setSelection(spinnerItemList1.indexOf(category))

// Set adapter for spinner2
            val spinnerAdapter2 =
                ArrayAdapter(requireContext(), R.layout.simple_spinner_item, spinnerItemList2)
            spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSpecialization.adapter = spinnerAdapter2
            spinnerSpecialization.setSelection(spinnerItemList2.indexOf(specialization))

// Set adapter for spinner3
            val spinnerAdapter3 =
                ArrayAdapter(requireContext(), R.layout.simple_spinner_item, spinnerItemList3)
            spinnerAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDetails.adapter = spinnerAdapter3
            spinnerDetails.setSelection(spinnerItemList3.indexOf(details))
            spinner.onItemSelectedListener = this@ProductDetailFragment
            spinnerSpecialization.onItemSelectedListener = this@ProductDetailFragment
            spinnerDetails.onItemSelectedListener = this@ProductDetailFragment
           // checkpricedetails()

            imageEdit.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "image/*"
                selectImagesActivityResult.launch(intent)
            }
            buttonUpdate.setOnClickListener {
                val productValidation = validateInformation()
                if (!productValidation) {
                    Toast.makeText(requireContext(), "Check your inputs", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(selectedImages.isEmpty()&& images1.isNotEmpty()){
                    val price1=binding.edPrice2.text.toString().trim()
                    val name = binding.edName.text.toString().trim()
                    val images = images1
                    val productDescription = binding.edDescription.text.toString().trim()
                    val price = binding.edPrice.text.toString().trim()
                    val offerPercentage = binding.edOfferPercentage.text.toString().trim()
                    val product = Product(
                        product1.id,
                        name,
                        category,
                        specialization,
                        price.toFloat(),
                        if(price1.isEmpty()) null else price1.toFloat(),
                        if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                        if (productDescription.isEmpty()) null else productDescription,
                        details,
                        images
                    )
                    viewModel.productUpdate(product,product1)
                    Toast.makeText(requireContext(),"Product Updated",Toast.LENGTH_SHORT).show()


                }else {
                    val price1 = binding.edPrice2.text.toString().trim()
                    val imagesByteArrays = getImagesByteArrays() //7
                    val name = binding.edName.text.toString().trim()
                    val images = mutableListOf<String>()
                    val productDescription = binding.edDescription.text.toString().trim()
                    val price = binding.edPrice.text.toString().trim()
                    val offerPercentage = binding.edOfferPercentage.text.toString().trim()

                    lifecycleScope.launch {
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
                        }

                        Log.d("test2", "test")
                        if(images.isNotEmpty()) {
                            val product = Product(
                                product1.id,
                                name,
                                category,
                                specialization,
                                price.toFloat(),
                                if (price1.isEmpty()) null else price1.toFloat(),
                                if (offerPercentage.isEmpty()) null else offerPercentage.toFloat(),
                                if (productDescription.isEmpty()) null else productDescription,
                                details,
                                images
                            )
                            viewModel.productUpdate(product, product1)
                            Toast.makeText(requireContext(), "Product Updated", Toast.LENGTH_SHORT)
                                .show()
                        }else{
                            Toast.makeText(requireContext(),"Product didn't uploaded try again",Toast.LENGTH_SHORT).show()
                        }


                    }

                }
            }
            buttonDelete.setOnClickListener {
                if (product1 != null) {
                    viewModel.deleteAddress(product1)
                    Toast.makeText(requireContext(),"Product Deleted",Toast.LENGTH_SHORT).show()
                }
                findNavController().navigateUp()
            }

        }
        viewPageAdapter.differ.submitList(product1.images)
    }
    //saving the data into firebase
    private fun saveProducts(state: (Boolean) -> Unit) {

    }

    private fun setUpViewPagerRv() {
        binding.apply {
            viewPagerProductImages.adapter = viewPageAdapter
        }
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        category = (binding.spinner.selectedItem as? String).toString()
        Log.e("selected", "${category}")
        specialization = (binding.spinnerSpecialization.selectedItem as? String).toString()
        Log.e("selected", "${specialization}")
        details = (binding.spinnerDetails.selectedItem as? String).toString()
        Log.e("selected", "${details}")
        checkpricedetails()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun checkpricedetails() {
        if (details.equals("KG")) {
            binding.edPrice.hint = "price/kg"
            binding.edPrice.setText(price)
            binding.edPrice2.visibility = View.GONE
        } else if (details.equals("Gram")) {
            Log.d("if statement", "${details}")
            binding.edPrice.hint = "price/gram"
            binding.edPrice.setText(price)
            binding.edPrice2.visibility = View.GONE
        } else if (details == "Piece") {
            binding.edPrice.hint = "price/piece"
            binding.edPrice.setText(price)
            binding.edPrice2.visibility = View.GONE
        } else if (details.equals("Bundle")) {
            binding.edPrice.hint = "price/Bundle"
            binding.edPrice.setText(price)
            binding.edPrice2.visibility = View.GONE
        } else if (details.equals("KG & Gram")) {
            binding.edPrice.hint = "price/kg"
            binding.edPrice.setText(price)
            binding.edPrice2.visibility = View.VISIBLE
            binding.edPrice2.hint = "price/Gram"
            binding.edPrice2.setText(price1)
        } else if (details.equals("Piece & Bundle")) {
            binding.edPrice.hint = "price/Piece"
            binding.edPrice.setText(price)
            binding.edPrice2.visibility = View.VISIBLE
            binding.edPrice2.hint = "price/Bundle"
            binding.edPrice2.setText(price1)
        }


    }
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


    private fun updateImages() {
        binding.tvSelectedImages.setText(selectedImages.size.toString())
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
    //validating
    private fun validateInformation(): Boolean {
        if (binding.edName.text.toString().trim().isEmpty())
            return false
        if (binding.edPrice.text.toString().trim().isEmpty())
            return false
        return true
    }
}

