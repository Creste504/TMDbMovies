package com.example.tmdbmovies.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tmdbmovies.MyRatingsFragment
import com.example.tmdbmovies.RecentSearchesFragment

class ProfileTabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2 // Total de abas

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RecentSearchesFragment()
            else -> MyRatingsFragment()
        }
    }
}