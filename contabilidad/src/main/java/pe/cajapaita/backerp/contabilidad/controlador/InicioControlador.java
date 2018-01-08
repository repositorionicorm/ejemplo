package pe.cajapaita.backerp.contabilidad.controlador;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Usuario;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;

/**
 *
 * @author dev-out-02
 */
@Controller
@RequestMapping("/")
public class InicioControlador {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView inicio() {
        ModelAndView modelAndView = null;
        modelAndView = new ModelAndView("logeo");
        return modelAndView;
    }

    @RequestMapping(value = "error", method = RequestMethod.GET)
    public ModelAndView error() {
        ModelAndView modelAndView = null;
        modelAndView = new ModelAndView("error");
        return modelAndView;
    }

    @RequestMapping(value = {"index","logout"}, method = RequestMethod.GET)
    public ModelAndView index(Authentication authentication) {
        UsuarioDTO usuario=(UsuarioDTO) authentication.getPrincipal();
        
        ModelAndView modelAndView= new ModelAndView("inicio");
        modelAndView.addObject("editar", usuario.getEditar());
        return modelAndView;
    }

    @RequestMapping(value = "denegado", method = RequestMethod.GET)
    public ModelAndView denegado() {
        return new ModelAndView("accesoDenegado");
    }

    @RequestMapping(value = "logeoFallo",method = RequestMethod.GET)
    public ModelAndView logeoFail(){
        ModelAndView modelAndView= new ModelAndView("logeo");
        modelAndView.addObject("mensaje",Mensaje.LOGEO_ERROR_CREDENCIALES);
        return modelAndView;
    }
    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ModelAndView login() {
        return new ModelAndView("logeo");
    }
    @RequestMapping(value = "ocurrioError", method = RequestMethod.GET)
    public ModelAndView errorPersonalizado(@RequestParam("mensaje") String mensaje) {
         ModelAndView modelAndView = new ModelAndView("errorPersonalizado");
         modelAndView.addObject("mensaje", mensaje);
         return modelAndView;
    }
    
}
