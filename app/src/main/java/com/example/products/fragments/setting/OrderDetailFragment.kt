package com.example.products.fragments.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakery.util.Resource
import com.example.bakery.util.VerticalItemDecoration
import com.example.bakery.util.hideBottomNavigationView
import com.example.bakery.util.showBottomNavigationView
import com.example.products.adapter.BillingProductAdapter
import com.example.products.databinding.FragmentOrderDetailBinding
import com.example.products.viewmodel.AllOrdersViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val billingProductsAdapter by lazy { BillingProductAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()
    val viewModel by viewModels<AllOrdersViewModel>()
    val firestore = Firebase.firestore
    var status:String=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val order = args.order

        setupOrderRv()
        lifecycleScope.launchWhenStarted {
            viewModel.editORders.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.progressbarAllOrders.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
        binding.apply {

            tvOrderId.text = "Order #${order.orderId}"


        imageCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }
            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.place}"
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = "₹ ${order.totalPrice}"

            buttonConfirm.setOnClickListener{

                status="Confirmed"
                viewModel.updateOrders(order,status)
                Toast.makeText(requireContext(),"Order confirmed",Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            buttonCancel.setOnClickListener {
                status="Canceled"
                viewModel.updateOrders(order,status)
                Toast.makeText(requireContext(),"Order canceled",Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            buttonDelvered.setOnClickListener {
                status="Delivered"
                viewModel.updateOrders(order,status)
                Toast.makeText(requireContext(),"Order canceled",Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }


        }

        billingProductsAdapter.differ.submitList(order.products)
    }

    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter = billingProductsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}