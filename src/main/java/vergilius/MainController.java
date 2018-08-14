package vergilius;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.yaml.snakeyaml.Yaml;

import org.yaml.snakeyaml.introspector.BeanAccess;
import vergilius.repos.TdataRepository;
import vergilius.repos.OsRepository;
import vergilius.repos.TtypeRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
public class MainController{
    @Autowired
    public OsRepository rep1;
    @Autowired
    public TtypeRepository rep2;
    @Autowired
    public TdataRepository rep3;

    @GetMapping("/login")
    public String displayLogin(Model model) throws IOException {
        List<Os> os = getListOs();

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));

        return "login";
    }
    @PostMapping("/login")
    public String handleLogin(@RequestParam(name="username") String username, @RequestParam(name="password") String password, HttpSession session, Model model) throws IOException {
        model.addAttribute(username);
        model.addAttribute(password);

        List<Os> os = getListOs();

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));

        return "login";
    }

    @GetMapping("/admin")
    public String displayAdmin(Model model) throws IOException {

        List<Os> os = getListOs();

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));

        return "admin";
    }

    @PostMapping("/admin")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        try(InputStream res = file.getInputStream()) {

            Yaml yaml = new Yaml();
            yaml.setBeanAccess(BeanAccess.FIELD);
            Root root = yaml.loadAs(res, Root.class);

            Os os = new Os();

            os.setOsname(root.getOsname());
            os.setFamily(root.getFamily());
            os.setTimestamp(root.getTimestamp());
            os.setBuildnumber(root.getBuildnumber());
            os.setArch(root.getArch());

            List<Ttype> types = root.getTypes();

            for(Ttype type: types)
            {
                type.setOpersys(os);
                Set<Tdata> datas = type.getData();

                if(datas != null)
                {
                    for(Tdata data: datas)
                    {
                        data.setTtype(type);
                    }
                }
            }
            os.setTypes(new HashSet<>(types));
            rep1.save(os);

        }
        catch(IOException e){}

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/admin";
    }

    public List<Os> getListOs()
    {
        List<Os> listOfOperSystems = new ArrayList<>();
        for(Os i : rep1.findAll())
        {
            listOfOperSystems.add(i);
        }
        return listOfOperSystems;
    }
/*
    public List<String> getListOfFamilies(List<Os> opers)
    {
        List<String> fam = new ArrayList<>(); // set allows only unique elements -> CHANGE LATER
        for(Os i: opers)
        {
            if(!fam.contains(i.getFamily()))
            {
                fam.add(i.getFamily());
            }
        }
        return fam;
    }
*/
    @GetMapping("/")
    public String displayHome(Model model)
    {
        List<Os> os = getListOs();

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));

        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));

        return "home";
    }

    @RequestMapping(value="/logout", method=RequestMethod.GET)
    public String logoutPage(Model model, HttpServletRequest request, HttpServletResponse response) {

        List<Os> os = getListOs();

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "/";
    }
    @GetMapping("/about")
    public String displayAbout(Model model)
    {
        List<Os> os = getListOs();

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));

        return "about";
    }

    @GetMapping("/arch")
    public String displayVersion(Model model)
    {
        List<Os> os = getListOs();

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));

        return "arch";
    }

    @GetMapping("/kernels")
    public String displaySpace(Model model)
    {
        List<Os> os = getListOs();

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));

        return "kernels";
    }

    @GetMapping("/kernelsx86")
    public String displaySpacex86(Model model)
    {
        List<Os> os = getListOs();

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));

        return "kernelsx86";
    }
    @GetMapping("/kernelsx64")
    public String displaySpacex64(Model model)
    {
        List<Os> os = getListOs();

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));

        return "kernelsx64";
    }

    @RequestMapping(value="/kernels/{famname:.+}")
    public String displayFamily(@PathVariable String famname, Model model)
    {
        List<Os> fam = rep1.findByFamily(famname);

        List<Os> os = getListOs();

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));

        model.addAttribute("fam", fam); //WHAT IS IT??????????

        return "family";
    }

    @RequestMapping(value = "/os/{osname:.+}", method = RequestMethod.GET)
    public String displayKinds(@PathVariable String osname, Model model)
    {
        Os opersys = rep1.findByOsname(osname);
        List<Ttype> reslist = rep2.findByOpersysAndIsConstFalseAndIsVolatileFalse(opersys);

        model.addAttribute("structs", Sorter.sortByName(Ttype.FilterByTypes(reslist, Ttype.Kind.STRUCT)));
        model.addAttribute("unions", Sorter.sortByName(Ttype.FilterByTypes(reslist, Ttype.Kind.UNION)));
        model.addAttribute("enums", Sorter.sortByName(Ttype.FilterByTypes(reslist, Ttype.Kind.ENUM)));

        model.addAttribute("osfam", rep1.findFamilyByOsname(osname));

        List<Os> os = getListOs();

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));

        return "ttype";
    }
    /* FieldBuilder!!! */
    @RequestMapping(value = "/os/{osname:.+}/type/{name}", method = RequestMethod.GET)
    public String displayType(@PathVariable String osname,@PathVariable String name, Model model)
    {
        Os opersys = rep1.findByOsname(osname);

        String link = "/os/" + osname + "/type/";

        List<Ttype> typeslist = rep2.findByNameAndOpersys(name, opersys);

        if(typeslist != null && !typeslist.isEmpty())
        {
            model.addAttribute("ttype", FieldBuilder.recursionProcessing(rep2, typeslist.get(0), 0, 0, link).toString());

            //search for cross-links
            List<Ttype> used_in = new ArrayList<>();

            for(Ttype i: typeslist)
            {
                used_in.addAll(rep2.findById1(i.getIdtype()));
                used_in.addAll(rep2.findById2(i.getIdtype()));
                used_in.addAll(rep2.findById3(i.getIdtype()));
                used_in.addAll(rep2.findById4(i.getIdtype()));
            }

            List<String> used_in_names = new ArrayList<>();
            for (Ttype i : used_in)
            {
                used_in_names.add(i.getName());
            }

            if (!used_in_names.isEmpty())
            {
                //Stream<String> stream = used_in_names.stream().distinct();
                used_in_names = used_in_names.stream().distinct().sorted().collect(Collectors.toList());
            }
            else
            {
                used_in_names = null;
            }
            model.addAttribute("cros", used_in_names);
        }
        List<Os> os = getListOs();

        Map<String, Integer> map = new HashMap<>();
        Map<Integer, String> mapInverted = new HashMap<>();
        for(int i = 1; i <= os.size(); i++)
        {
            map.put(os.get(i - 1).getOsname(), i);
            mapInverted.put(i, os.get(i - 1).getOsname());
        }

        model.addAttribute("osfam", rep1.findFamilyByOsname(osname));

        model.addAttribute("os", os);
        //model.addAttribute("families", getListOfFamilies(os));
        model.addAttribute("fam86", rep1.findByArch("x86"));
        model.addAttribute("fam64", rep1.findByArch("x64"));
        model.addAttribute("mapos", map);
        model.addAttribute("invertMapos", mapInverted);

        return "tdata";
    }
}

