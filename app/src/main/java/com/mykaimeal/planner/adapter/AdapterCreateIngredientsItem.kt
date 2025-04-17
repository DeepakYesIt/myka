package com.mykaimeal.planner.adapter

import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mykaimeal.planner.R
import com.mykaimeal.planner.databinding.AdapterCreateIngredientsItemBinding
import com.mykaimeal.planner.fragment.mainfragment.addrecipetab.createrecipefromimage.model.RecyclerViewItemModel

class AdapterCreateIngredientsItem(private var datalist: MutableList<RecyclerViewItemModel>, private var requireActivity: FragmentActivity,
                                   private var uploadImage: UploadImage
) : RecyclerView.Adapter<AdapterCreateIngredientsItem.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: AdapterCreateIngredientsItemBinding =
            AdapterCreateIngredientsItemBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = datalist[position]

        if (item.ingredientName!=null){
            holder.binding.etAddIngredients.setText(item.ingredientName.toString())
        }

        if (item.quantity!=null){
            holder.binding.etAddIngQuantity.setText(item.quantity.toString())
        }

        holder.binding.relImages.setOnClickListener{
            uploadImage.uploadImage(holder.binding.imageData, position, holder.binding.imgCross,holder.binding.layProgess.root )
        }

        holder.binding.spinnerQntType.setItems(
            listOf("tsp", "tbsp", "cup", "ml", "liter", "fl oz","unit", "Pint", "quart", "gallon", "gram", "kg", "mg", "ounce", "pound",
                "pinch", "dash", "drop", "handful", "slice", "stick", "piece", "can", "bottle", "jar", "packet", "bunch", "sprig", "inch", "cm", "feet")
        )

        if (item.measurement!=null){
            holder.binding.spinnerQntType.setText(item.measurement.toString())
        }

        holder.binding.spinnerQntType.setOnSpinnerItemSelectedListener<String> { _, _, _, selectedItem ->
            item.measurement = selectedItem  // Update the model when user selects an item
        }

        item.measurement=holder.binding.spinnerQntType.text.toString().trim()

        holder.binding.imgCross.setOnClickListener{

        }

        if (item.uri!=null){
            Glide.with(requireActivity)
                .load(item.uri)
                .error(R.drawable.upload_ing_icon)
                .placeholder(R.drawable.upload_ing_icon)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.layProgess.root.visibility= View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.binding.layProgess.root.visibility= View.GONE
                        return false
                    }
                })
                .into(holder.binding.imageData)
        }else{
            holder.binding.layProgess.root.visibility= View.GONE
        }

        // Update the model when quantity changes
        holder.binding.etAddIngredients.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                item.ingredientName = s.toString()
                updateBackground(holder.binding.llLayouts, s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

   // Update the model when quantity changes
        holder.binding.etAddIngQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                item.quantity = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateBackground(llLayouts: LinearLayout, text: String) {
        if (text.isNotEmpty()) {
            llLayouts.setBackgroundResource(R.drawable.create_select_bg) // Change this drawable
        } else {
            llLayouts.setBackgroundResource(R.drawable.create_unselect_bg)  // Default background
        }
    }

    // Function to check if all EditTexts are filled
    fun isAllFieldsFilled(): Boolean {
        return datalist.all { it.ingredientName.isNotEmpty() }
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    // Function to highlight empty fields
    fun highlightEmptyFields(recyclerView: RecyclerView) {
        for (i in datalist.indices) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as? ViewHolder
            holder?.binding!!.etAddIngredients.let { editText ->
               /* if (datalist[i].ingredientName.trim().isEmpty()) {
                }*/
            }
        }
    }

    fun addNewItem() {
        datalist.add(RecyclerViewItemModel("","",false,"","")) // Add a blank item
        notifyItemInserted(datalist.size - 1)
    }

    fun update(toMutableList: MutableList<RecyclerViewItemModel>) {
        this.datalist=toMutableList
        notifyDataSetChanged()

    }

    class ViewHolder(var binding: AdapterCreateIngredientsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    interface UploadImage {
        // all are the abstract methods.
        fun uploadImage(imageView: ImageView?, pos: Int, cross: ImageView?, root: RelativeLayout)
        fun crossImage(cross: ImageView?, imageView: ImageView?, textView: TextView?, pos: Int)
    }


}