/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.samples.apps.sunflower.adapters.PlantAdapter
import com.google.samples.apps.sunflower.databinding.FragmentPlantListBinding
import com.google.samples.apps.sunflower.viewmodels.PlantListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlantListFragment : Fragment() {

    private val viewModel: PlantListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPlantListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val adapter = PlantAdapter()
        binding.plantList.adapter = adapter
        subscribeUi(adapter)

        setHasOptionsMenu(true)
        return binding.root
    }

    // menu inflate
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_plant_list, menu)
    }

    // 옵션 아이템을 선택했을때 실행할 동작 설정
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter_zone -> {
                updateData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun subscribeUi(adapter: PlantAdapter) {
        // viewModel 에서 plants 를 가져와서 adapter 로 submit
        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            adapter.submitList(plants)
        }
    }

    // filter 정보에 따라 보여주는 데이터 변경
    private fun updateData() {
        with(viewModel) {
            if (isFiltered()) {
                clearGrowZoneNumber() // glow zone 을 -1 로 설정
            } else {
                setGrowZoneNumber(2) // glow zone 을 9 로 설정
            }
        }
    }
}
