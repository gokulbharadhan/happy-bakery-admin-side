package com.example.bakery.fragments.shopping

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakery.util.Resource
import com.example.bakery.util.VerticalItemDecoration
import com.example.bakery.util.showBottomNavigationView
import com.example.products.R
import com.example.products.adapter.CartProductAdapter
import com.example.products.databinding.FragmentCartBinding
import com.example.products.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
class CartFragment: Fragment(R.layout.fragment_cart) {
    private lateinit var binding:FragmentCartBinding
    private val cartAdapter by lazy{ CartProductAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartRv()
        cartAdapter.onProdcutClick={
            val b=Bundle().apply{putParcelable("product",it)}
            findNavController().navigate(R.id.action_cartFragment_to_productDetailFragment,b)
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
                            cartAdapter.differ.submitList(it.data)
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
            layoutManager=LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
            adapter=cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}