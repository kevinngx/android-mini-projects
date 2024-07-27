package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

  /**
   * Called when the Fragment is ready to display content to the screen.
   *
   * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
   */
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    // Get a reference to the binding object and inflate the fragment views.
    val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
      inflater, R.layout.fragment_sleep_tracker, container, false
    )

    val application = requireNotNull(this.activity).application
    val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
    val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

    val sleepTrackerViewModel =
      ViewModelProvider(
        this, viewModelFactory
      ).get(SleepTrackerViewModel::class.java)

    binding.sleepTrackerViewModel = sleepTrackerViewModel

    // Setting uo Grid Layout manager with 3 spans
    val manager = GridLayoutManager(activity, 3)
    binding.sleepList.layoutManager = manager

    val adapter = SleepNightAdapter(SleepNightListener { nightId ->
      sleepTrackerViewModel.onSleepNightClicked(nightId)
    })

    binding.sleepList.adapter = adapter
    // TODO FIX THIS
    sleepTrackerViewModel.nights.observe(viewLifecycleOwner, Observer {
      it?.let {
        adapter.submitList(it)
      }
    })

    binding.setLifecycleOwner(this)

    sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner, Observer { night ->
      night?.let {
        this.findNavController().navigate(
          SleepTrackerFragmentDirections
            .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId)
        )
        sleepTrackerViewModel.doneNavigating()
      }
    })

    sleepTrackerViewModel.navigateToSleepDataQuality.observe(viewLifecycleOwner, Observer {night ->
      night?.let {
        this.findNavController().navigate(
          SleepTrackerFragmentDirections
          .actionSleepTrackerFragmentToSleepDetailFragment(night))
        sleepTrackerViewModel.onSleepDataQualityNavigated()
      }
    })

    return binding.root
  }
}