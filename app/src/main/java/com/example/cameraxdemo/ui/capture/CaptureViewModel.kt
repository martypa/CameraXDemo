package com.example.cameraxdemo.ui.capture

import android.content.SharedPreferences
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class CaptureViewModel : ViewModel() {


   private val userLiveData = MutableLiveData<Boolean>()

   fun getUser(): LiveData<Boolean> {
      return userLiveData
   }


   fun doAction(newState: Boolean) {
      this.userLiveData.value = newState
   }


}