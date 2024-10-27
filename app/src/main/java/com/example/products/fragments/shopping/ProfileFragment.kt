package com.example.bakery.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakery.util.Resource
import com.example.bakery.util.VerticalItemDecoration
import com.example.products.R
import com.example.products.adapter.AllUserAdapter
import com.example.products.adapter.CartProductAdapter
import com.example.products.databinding.FragmentProfileBinding
import com.example.products.viewmodel.AllUserViewModel
import com.example.products.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val allUser by lazy{ AllUserAdapter() }
    private var imageUri: Uri? = null
    private val viewModel by activityViewModels<AllUserViewModel>()
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartRv()
        allUser.onProdcutClick={
            val b=Bundle().apply{putParcelable("user",it)}
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment,b)
        }
        lifecycleScope.launchWhenStarted {

            viewModel.Products.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.progressbarCart.visibility=View.VISIBLE
                    }
                    is Resource.Success->{
                        binding.progressbarCart.visibility=View.INVISIBLE
                        if(it.data!!.isEmpty()){
                            showEmptyCart()
                        }else{
                            Log.e("new updation","${it.data}")
                            hideEmptyCart()
                            allUser.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error->{
                        binding.progressbarCart.visibility=View.VISIBLE
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else->Unit
                }
            }
        }
        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun hideEmptyCart() {
        binding.apply {
            layoutCarEmpty.visibility=View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCarEmpty.visibility=View.VISIBLE
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply{
            layoutManager= LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            adapter=allUser
            addItemDecoration(VerticalItemDecoration())
        }
    }
}