package pe.cajapaita.backerp.contabilidad.configuracion;

/**
 *
 * @author aguilar
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.servicio.IAccesoServicio;

/**
 *
 *
 * @author hnole
 *
 */
@Service
public class Autenticar implements AuthenticationProvider {

    public static String usuario;
    public static String urlService;

    private final Logger logger = Logger.getLogger(Autenticar.class);

    @Autowired
    private IAccesoServicio accesoServicio;

    @Value("${webservice.urlda}")
    public void setUrlService(String urlService) {
        this.urlService = urlService;
    }

    public String getUrlService() {
        return urlService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String name = authentication.getName();
            usuario = name;
            String password = authentication.getCredentials().toString();
            if (validateUser(name, password)) {
                UsuarioDTO usuarioDTO = accesoServicio.traerUsuario(name);
                if (usuarioDTO == null) {
                    return null;
                }

                List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
                grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
                
                UsernamePasswordAuthenticationToken userPasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(usuarioDTO, name, grantedAuths);
                userPasswordAuthenticationToken.setDetails(name);
                Authentication auth = userPasswordAuthenticationToken;
                
                return auth;
            } else {
                return null;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    //<editor-fold defaultstate="collapsed" desc="metodos privados"> 
    private static boolean validateUser(java.lang.String userName, java.lang.String password) {

        boolean validar = false;
        String tempUrl;
        try {

            tempUrl = urlService.replace("user", userName);
            tempUrl = tempUrl.replace("password", password);

            URL url = new URL(tempUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            if (br.readLine().equalsIgnoreCase("True")) {
                validar = true;
            }
            conn.disconnect();
        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return validar;

    }

//    static String Encriptar(String pass) {
//        try {
//            String llave = "4d89g13j4j91j27c582ji693";
//            String init = "metaphor";
//            byte[] plaintext = pass.getBytes();
//            byte[] tdesKeyData = llave.getBytes();
//            byte[] myIV = init.getBytes();
//            Cipher c3des = Cipher.getInstance("DESede/CBC/PKCS5Padding");
//            SecretKeySpec myKey = new SecretKeySpec(tdesKeyData, "DESede");
//            IvParameterSpec ivspec = new IvParameterSpec(myIV);
//            c3des.init(Cipher.ENCRYPT_MODE, myKey, ivspec);
//            byte[] cipherText = c3des.doFinal(plaintext);
//            byte[] base64Bytes = Base64.encode(cipherText);
//            String base64EncryptedString = new String(base64Bytes);
//            return base64EncryptedString;
//        } catch (Exception ce) {
//            ce.printStackTrace();
//            return ce.getMessage();
//        }
//
//    }
    //</editor-fold>
}
