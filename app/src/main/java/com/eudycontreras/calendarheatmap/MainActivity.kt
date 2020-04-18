package com.eudycontreras.calendarheatmap

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.eudycontreras.calendarheatmap.databinding.MainBinding

internal class MainActivity : AppCompatActivity() {

    private val viewModel: SomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: MainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }
    }
}
