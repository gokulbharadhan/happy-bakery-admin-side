package com.example.products.adapter

import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bakery.data.CartProduct
import com.example.bakery.helper.getProductPrice
import com.example.products.databinding.BillingProductsRvItemBinding

class BillingProductAdapter:RecyclerView.Adapter<BillingProductAdapter.BillingProductViewHolder>() {
    var quantityitem:Double=0.0
    inner class BillingProductViewHolder(val binding: BillingProductsRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(billingProduct:CartProduct) {
                binding.apply{
                    Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)
                    tvProductCartName.text=billingProduct.product.name
                    quantityitem= billingProduct.quantity
                    Log.e("cartProduct","$quantityitem")
                    tvBillingProductQuantity.text=quantityitem.toString()
                    type.text=billingProduct.type.toString()
                    val productprice:Float
                    var incrementval:Double
                    if(billingProduct.type.equals("KG"))
                        productprice= billingProduct.product.price!!
                    else if(billingProduct.type.equals("Gram"))
                        productprice= billingProduct.product.price1!!
                    else if(billingProduct.type.equals("Piece"))
                        productprice= billingProduct.product.price!!
                    else
                        productprice= billingProduct.product.price1!!
                    val priceAfterPercentage=billingProduct.product.offerPercentage.getProductPrice(productprice)
                    tvProductCartPrice.text="₹ ${String.format("%.2f",priceAfterPercentage)}"
                }
            }

        }
    private val diffUtil=object: DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem

        }
    }

    val differ=AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductViewHolder {
        return BillingProductViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BillingProductViewHolder, position: Int) {
        val billingProduct=differ.currentList[position]
        holder.bind(billingProduct)
        holder.itemView.setOnClickListener{
            onClick?.invoke(billingProduct)
        }
    }
    var onClick: ((CartProduct)->Unit)?=null

}