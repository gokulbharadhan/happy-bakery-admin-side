package com.example.products.fragments.shopping

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.bakery.util.hideBottomNavigationView
import com.example.products.databinding.FragmentUserAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserAccountFragment: Fragment(){
    private val args by navArgs<UserAccountFragmentArgs>()
    private lateinit var binding:FragmentUserAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user= args.user
        binding.apply {
            Glide.with(this@UserAccountFragment).load(user?.imagePath).error(ColorDrawable(Color.BLACK)).into(imageUser)
            edFirstName.setText(user?.firstName)
            edLastName.setText(user?.lastName)
            edEmail.setText(user?.email)
            imageCloseUserAccount.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }



}