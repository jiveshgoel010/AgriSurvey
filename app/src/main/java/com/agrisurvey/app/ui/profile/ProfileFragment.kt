package com.agrisurvey.app.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agrisurvey.app.R
import com.agrisurvey.app.databinding.FragmentProfileBinding
import com.agrisurvey.app.ui.auth.PhoneAuthActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ProfileFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        recyclerView = binding.menuRecyclerView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserData()
        setupMenuList()
    }

    private fun loadUserData() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val name = document.getString("name") ?: "Unknown"
                val email = document.getString("email") ?: "Not available"
                binding.profileName.text = getString(R.string.name_with_hyphen) + "$name"
                binding.profileEmail.text = getString(R.string.email_with_hyphen) + "$email"
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load user info", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupMenuList() {
        val menuItems = listOf(
            ProfileMenuItem(
                R.drawable.terms,
                getString(R.string.terms_and_conditions),
                ProfileMenuActions.TERMS
            ),
            ProfileMenuItem(
                R.drawable.baseline_contact_support_24,
                getString(R.string.contact_support),
                ProfileMenuActions.SUPPORT
            ),
            ProfileMenuItem(
                R.drawable.baseline_logout_24,
                getString(R.string.action_logout),
                ProfileMenuActions.LOGOUT
            )
        )

        val adapter = ProfileMenuAdapter(menuItems) { actionId ->
            when (actionId) {
                ProfileMenuActions.TERMS -> {
                    Toast.makeText(requireContext(), "Terms clicked", Toast.LENGTH_SHORT).show()
                }

                ProfileMenuActions.SUPPORT -> {
                    Toast.makeText(requireContext(), "Support clicked", Toast.LENGTH_SHORT).show()
                }

                ProfileMenuActions.LOGOUT -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(requireContext(), PhoneAuthActivity::class.java))
                    requireActivity().finish()
                }
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
}

data class ProfileMenuItem(
    val iconResId: Int,
    val title: String,
    val actionId: Int
)

object ProfileMenuActions {
    const val TERMS = 1
    const val SUPPORT = 2
    const val LOGOUT = 3
}

class ProfileMenuAdapter(
    private val items: List<ProfileMenuItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ProfileMenuAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon)
        val title: TextView = view.findViewById(R.id.title)

        init {
            view.setOnClickListener {
                val actionId = items[adapterPosition].actionId
                onItemClick(actionId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_menu, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.iconResId)
        holder.title.text = item.title
    }


}