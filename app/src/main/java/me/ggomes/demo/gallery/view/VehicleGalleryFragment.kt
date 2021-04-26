package me.ggomes.demo.gallery.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.ggomes.demo.R
import me.ggomes.demo.common.views.fragments.VehicleListFragmentDirections
import me.ggomes.demo.databinding.FragmentVehicleGalleryBinding
import me.ggomes.demo.gallery.models.Vehicle
import me.ggomes.demo.gallery.viewmodel.VehicleListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class VehicleGalleryFragment: Fragment() {

    private val vehicleListViewModel: VehicleListViewModel by viewModel()
    private lateinit var galleryViewBinding: FragmentVehicleGalleryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        galleryViewBinding = FragmentVehicleGalleryBinding.inflate(inflater , container, false)
        return galleryViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = galleryViewBinding.galleryRecyclerview
        recycler.layoutManager = GridLayoutManager(
            requireContext(),
            2,
            RecyclerView.VERTICAL,
            false)

        vehicleListViewModel.errorLiveData.observe(viewLifecycleOwner) {
            presentErrorToast(requireContext().getString(R.string.error_data_retrieval))
        }

        galleryViewBinding.progressBar.visibility = View.VISIBLE
        vehicleListViewModel.getVehicleById()

        vehicleListViewModel.carImagesLiveData.observe(viewLifecycleOwner) {
            galleryViewBinding.progressBar.visibility = View.GONE
            val recyclerAdapter = VehicleGridRecyclerAdapter(it, ::navigateToMovieDetails)
            recycler.adapter = recyclerAdapter
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun navigateToMovieDetails(vehicle: Vehicle) {
        if (vehicle.uri != null) {
            val action =
                VehicleListFragmentDirections.actionVehicleListFragmentToLargePictureDetailsFragment(
                    vehicle.uri
                )

            findNavController().navigate(action)
        } else {
            presentErrorToast(getString(R.string.error_cant_navigate_to_details))
        }
    }

    private fun presentErrorToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT)
            .show()
    }
}