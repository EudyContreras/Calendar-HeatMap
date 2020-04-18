package com.eudycontreras.calendarheatmap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eudycontreras.calendarheatmaplibrary.framework.data.HeatMapData

internal class SomeViewModel : ViewModel() {

    val demoData: LiveData<HeatMapData> = MutableLiveData(

    )
}
