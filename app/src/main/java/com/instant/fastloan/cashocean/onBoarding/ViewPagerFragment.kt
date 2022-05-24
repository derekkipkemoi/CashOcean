package com.toploans.cashOcean.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.toploans.cashOcean.R
import com.toploans.cashOcean.databinding.FragmentViewPagerBinding
import com.toploans.cashOcean.onBoarding.adapter.OnBoardingItemsAdapter
import com.toploans.cashOcean.onBoarding.dataClasses.OnBoardingItemDataClass
import com.toploans.cashOcean.utils.SharedPreferenceUtils
import com.toploans.cashOcean.utils.ZoomOutPageTransformer


class ViewPagerFragment : Fragment(), OnBoardingItemsAdapter.OnItemClickListener {
    private lateinit var adapter: OnBoardingItemsAdapter
    private lateinit var binding: FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_pager, container, false)
        val onBoardingData1 =
            OnBoardingItemDataClass(
                "Apply for a loan through your mobile phone",
                "Now you only need a mobile phone to get a loan immediately, which is both safe and convenient",
                R.drawable.apply
            )
        val onBoardingData2 =
            OnBoardingItemDataClass(
                "Receive your loan on time",
                "Easily receive your loan directly to your mobile account within a short time of your application",
                R.drawable.receive
            )
        val onBoardingData3 =
            OnBoardingItemDataClass(
                "We reward your for paying on time",
                "Every time you repay a loan on time, you will be rewarded with interest or promotion",
                R.drawable.pay
            )

        val sharedPreferenceUtils = SharedPreferenceUtils(this.requireContext())
        if (sharedPreferenceUtils.getRegistered()) {
            findNavController().navigate(R.id.loginFragment)
        } else {
            val onBoardingArrayList = ArrayList<OnBoardingItemDataClass>()
            onBoardingArrayList.add(onBoardingData1)
            onBoardingArrayList.add(onBoardingData2)
            onBoardingArrayList.add(onBoardingData3)

            adapter = OnBoardingItemsAdapter(onBoardingArrayList, this)
            binding.viewPager.adapter = adapter
            binding.indicator.setViewPager(binding.viewPager)


            binding.viewPager.setPageTransformer(ZoomOutPageTransformer())
        }


        return binding.root


    }

    override fun onItemClick(position: Int) {
        if (position == 2) {
            findNavController().navigate(R.id.policyFragment)
        }
    }

}