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
import me.ggomes.demo.databinding.FragmentGalleryBinding
import me.ggomes.demo.gallery.models.GalleryImage
import me.ggomes.demo.gallery.viewmodel.GalleryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryFragment: Fragment() {

    private val galleryViewModel: GalleryViewModel by viewModel()
    private lateinit var galleryViewBinding: FragmentGalleryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        galleryViewBinding = FragmentGalleryBinding.inflate(inflater , container, false)
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

        galleryViewModel.errorLiveData.observe(viewLifecycleOwner) {
            presentErrorToast(it)
        }

        galleryViewBinding.progressBar.visibility = View.VISIBLE
        galleryViewModel.getVehicleById()

        galleryViewModel.carImagesLiveData.observe(viewLifecycleOwner) {
            galleryViewBinding.progressBar.visibility = View.GONE
            val recyclerAdapter = GalleryGridAdapter(it, ::navigateToMovieDetails)
            recycler.adapter = recyclerAdapter
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun navigateToMovieDetails(galleryImage: GalleryImage) {
        if (galleryImage.uri != null) {
            val action =
                GalleryFragmentDirections.actionVehicleListFragmentToLargePictureDetailsFragment(
                    galleryImage.uri
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