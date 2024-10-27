package com.example.products.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakery.data.Product
import com.example.bakery.data.User
import com.example.products.databinding.CartProductItemBinding
import com.example.products.databinding.FragmentProfileBinding
import com.example.products.databinding.UserViewFragmentBinding

class AllUserAdapter : RecyclerView.Adapter<AllUserAdapter.AllUserViewHolder>() {
    inner class AllUserViewHolder(val binding: UserViewFragmentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(user: User){
            binding.apply {
                Glide.with(itemView).load(user.imagePath).error(ColorDrawable(Color.BLACK)).into(imageCartProduct)
                tvProductCartName.text= user.firstName
                tvprice1.text=user.email
            }
        }
    }
    private val diffCallback=object: DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem==newItem
        }


    }
    val differ= AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllUserViewHolder {
        Log.e("onCrateView","oncrate view")
        return AllUserViewHolder(
            UserViewFragmentBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }


    override fun onBindViewHolder(holder: AllUserViewHolder, position: Int) {
        val user=differ.currentList[position]
        holder.bind(user)
        holder.itemView.setOnClickListener{
            onProdcutClick?.invoke(user)
        }

    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onProdcutClick:((User) -> Unit)?=null



}