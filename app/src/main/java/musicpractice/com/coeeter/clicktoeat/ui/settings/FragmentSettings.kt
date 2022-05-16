<<<<<<< HEAD:app/src/main/java/musicpractice/com/coeeter/clicktoeat/ui/settings/FragmentSettings.kt
package musicpractice.com.coeeter.clicktoeat.ui.settings
=======
package musicpractice.com.coeeter.clicktoeat.ui.fragments
>>>>>>> master:app/src/main/java/musicpractice/com/coeeter/clicktoeat/ui/fragments/FragmentSettings.kt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
<<<<<<< HEAD:app/src/main/java/musicpractice/com/coeeter/clicktoeat/ui/settings/FragmentSettings.kt
import musicpractice.com.coeeter.clicktoeat.R
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentSettingsBinding
import musicpractice.com.coeeter.clicktoeat.ui.auth.LoginActivity
import musicpractice.com.coeeter.clicktoeat.utils.removeItemFromSharedPref
=======
import musicpractice.com.coeeter.clicktoeat.databinding.FragmentSettingsBinding
import musicpractice.com.coeeter.clicktoeat.ui.activities.LoginActivity
>>>>>>> master:app/src/main/java/musicpractice/com/coeeter/clicktoeat/ui/fragments/FragmentSettings.kt

class FragmentSettings : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.logout.setOnClickListener {
            requireActivity().apply {
                removeItemFromSharedPref(
                    getString(R.string.sharedPrefName),
                    getString(R.string.sharedPrefToken),
                    getString(R.string.sharedPrefProfile)
                )
                startActivity(Intent(this, LoginActivity::class.java))
                overridePendingTransition(0, 0)
                finish()
            }
        }
        return binding.root
    }

}