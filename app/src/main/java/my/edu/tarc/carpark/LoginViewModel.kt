package my.edu.tarc.carpark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _isAdminLoggedIn = MutableStateFlow(false)

    private val _email = MutableStateFlow<String?>(null)
    val email: StateFlow<String?> = _email

    private val _adminId = MutableStateFlow<String?>(null)

    fun login(email: String) {
        viewModelScope.launch {
            _isLoggedIn.value = true
            _email.value = email
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoggedIn.value = false
            _email.value = null
        }
    }

    fun adminLogout() {
        viewModelScope.launch {
            _isAdminLoggedIn.value = false
            _adminId.value = null
        }
    }
}