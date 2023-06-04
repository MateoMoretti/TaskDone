package mis.finanzas.diarias.Acceso;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import mis.finanzas.diarias.DataBase;
import com.example.taskdone.R;
import mis.finanzas.diarias.Utils;
import com.example.taskdone.databinding.FragmentCrearCuentaBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CrearCuentaFragment extends Fragment {
    private FragmentCrearCuentaBinding mBinding;
    private NavController mNavController;
    private DataBase mDatabase;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentCrearCuentaBinding.inflate(inflater, container, false);
        mNavController = NavHostFragment.findNavController(this);
        mDatabase = new DataBase(requireContext());

        mBinding.crearCuenta.setOnClickListener(v -> crearCuenta());

        mBinding.contrasena.setInputType(129);
        mBinding.verPassword.setOnClickListener(v -> togglePasswordVisibility());

        mBinding.volver.setOnClickListener(v -> mNavController.navigate(R.id.action_crearCuentaFragment_to_loginFragment));

        mBinding.ayuda.setOnClickListener(v -> showHelpDialog());

        return mBinding.getRoot();
    }

    private void crearCuenta() {
        Cursor data;
        boolean userExists = false;
        String username = mBinding.usuario.getText().toString();
        String password = mBinding.contrasena.getText().toString();

        try {
            data = mDatabase.getUserByUsername(username);
            userExists = data != null && data.moveToNext();
        } catch (CursorIndexOutOfBoundsException e) {
            Log.e("CrearCuentaFragment", Objects.requireNonNull(e.getMessage()));
        }

        if (userExists) {
            Toast.makeText(requireContext(), R.string.error_usuario_existente, Toast.LENGTH_SHORT).show();
        } else {
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(requireContext(), R.string.error_usuario_vacio, Toast.LENGTH_SHORT).show();
            } else {
                boolean result = mDatabase.addUser(username, password);
                if (result) {
                    Toast.makeText(requireContext(), R.string.usuario_creado, Toast.LENGTH_SHORT).show();
                    mNavController.navigate(R.id.action_crearCuentaFragment_to_loginFragment);
                } else {
                    Toast.makeText(requireContext(), R.string.error_crear_usuario, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void togglePasswordVisibility() {
        int inputType = mBinding.contrasena.getInputType();
        if (inputType == 129) {
            mBinding.contrasena.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            mBinding.contrasena.setInputType(129);
        }
    }

    private void showHelpDialog() {
        ArrayList<String> messageList = new ArrayList<>(Arrays.asList(
                getResources().getString(R.string.ayuda_crear_cuenta_1),
                getResources().getString(R.string.ayuda_crear_cuenta_2)
        ));
        Utils.popupAyuda(requireContext(), requireActivity(), messageList);
    }
}