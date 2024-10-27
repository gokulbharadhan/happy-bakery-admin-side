package com.example.products.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakery.data.Product
import com.example.products.databinding.CartProductItemBinding

class CartProductAdapter : RecyclerView.Adapter<CartProductAdapter.CartProductViewHolder>() {
    inner class CartProductViewHolder(val binding: CartProductItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imageCartProduct)
                tvProductCartName.text= product.name
                val size=product.details
                if(size.equals("KG") || size.equals("Gram") || size.equals("Piece") || size.equals("Bundle")){
                    tvprice1.text="₹ ${product.price}/${product.details}"
                }else if(size.equals("KG & Gram")){
                    tvprice1.text="₹ ${product.price}/KG"
                    tvprice2.text="₹ ${product.price1}/Gram"
                }else{
                    tvprice1.text="₹ ${product.price}/Piece"
                    tvprice2.text="₹ ${product.price1}/Bundle"
                }


            }
        }
    }
    private val diffCallback=object: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            Log.e("areItemsThesame","are item same")
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            Log.e("areContentsTheSane","are content the same")
            return oldItem==newItem
        }

    }
    val differ= AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        Log.e("onCrateView","oncrate view")
        return CartProductViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }


    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        val product=differ.currentList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener{
            onProdcutClick?.invoke(product)
        }

    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onProdcutClick:((Product) -> Unit)?=null



}