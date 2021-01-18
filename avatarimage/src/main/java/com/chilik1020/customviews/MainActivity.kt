package com.chilik1020.customviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chilik1020.customviews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
   //     binding.aivUserAvatar.setImageDrawable(getDrawable(R.drawable.avatar_img))
    }
}