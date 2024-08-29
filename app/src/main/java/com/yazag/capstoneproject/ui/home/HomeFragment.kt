package com.yazag.capstoneproject.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yazag.capstoneproject.R
import com.yazag.capstoneproject.common.gone
import com.yazag.capstoneproject.common.viewBinding
import com.yazag.capstoneproject.common.visible
import com.yazag.capstoneproject.data.model.response.ProductUI
import com.yazag.capstoneproject.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val viewModel by viewModels<HomeViewModel>()

    private val productAdapter = ProductsAdapter(onProductClick = ::onProductClick, onFavClick = ::onFavClick)

    private val saleProductsAdapter = SaleProductsAdapter(onProductClick = ::onProductClick, onFavClick = ::onFavClick)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProducts()

        with(binding) {
            rvProducts.adapter = productAdapter

            ivSignOut.setOnClickListener {
                viewModel.signOut()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        observeData()
    }

    private fun observeData() = with(binding) {
        viewModel.productState.observe(viewLifecycleOwner) { state ->
            when (state) {
                ProductState.Loading -> productProgressBar.visible()

                is ProductState.SuccessState -> {
                    productProgressBar.gone()
                    productAdapter.submitList(state.products)
                }

                is ProductState.EmptyScreen -> {
                    productProgressBar.gone()
                    ivProductEmpty.visible()
                    tvProductEmpty.visible()
                    rvProducts.gone()
                    tvProductEmpty.text = state.failMessage
                }

                is ProductState.ShowPopUp -> {
                    productProgressBar.gone()
                    Snackbar.make(requireView(), state.errorMessage, 1000).show()
                }
                ProductState.GoToSignIn -> {
                    findNavController().navigate(R.id.homeToMain)
                }
            }
        }
    }

    private fun onProductClick(id: Int) {
        findNavController().navigate(R.id.detailFragment)     }

    private fun onFavClick(product: ProductUI) {
        viewModel.setFavoriteState(product)
    }
}