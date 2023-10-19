package com.example.museodelokal


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class PostAdapter(val context: Context, val postList: ArrayList<Data_Upload>, ) :
    RecyclerView.Adapter<PostAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.userpost, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = postList[position]
        val title = currentUser.getuTitle()
        val fullname = currentUser.getuFullname()
        val price = currentUser.getuPrice()
        val desc = currentUser.getuDescription()
        val img = currentUser.getuImageUrl()

        holder.price.text = price.toString()
        holder.title.text = price.toString()


        holder.itemView.setOnClickListener{
            val intent = Intent(context,MessageActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("fullname", fullname)
            intent.putExtra("price", price)
            intent.putExtra("desc", desc)
            intent.putExtra("img", img)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.product_name)
        val img = itemView.findViewById<TextView>(R.id.image)
        val price = itemView.findViewById<TextView>(R.id.price)
    }

}