package com.example.demo.controller;

import com.example.demo.model.GhostNet;
import com.example.demo.model.Person;
import com.example.demo.repository.GhostNetRepository;
import com.example.demo.repository.PersonRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/nets")
public class GhostNetController {

    private final GhostNetRepository nets;
    private final PersonRepository persons;

    public GhostNetController(GhostNetRepository nets, PersonRepository persons) {
        this.nets = nets;
        this.persons = persons;
    }

    @InitBinder("person")
    public void initPersonBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id");
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("nets", nets.findAll());
        return "list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("net", new GhostNet());
        return "form";
    }

    @PostMapping
    public String create(@ModelAttribute GhostNet net, Model model) {
        net.setStatus(GhostNet.Status.GEMELDET);

        if (net.getStatus() == GhostNet.Status.VERSCHOLLEN && net.isAnonymousReport()) {
            model.addAttribute("net", net);
            model.addAttribute("error",
                    "Verschollen kann nicht anonym gemeldet werden. Bitte Name (und ggf. Telefon) angeben.");
            return "form";
        }

        if (net.isAnonymousReport()) {
            net.setReporterName(null);
            net.setReporterPhone(null);
        } else if (net.getReporterName() == null || net.getReporterName().isBlank()) {
            model.addAttribute("net", net);
            model.addAttribute("error", "Bitte den Namen der meldenden Person angeben (oder anonym wählen).");
            return "form";
        }

        nets.save(net);
        return "redirect:/nets";
    }

    @GetMapping("/{id}/assign")
    public String assignForm(@PathVariable Long id, Model model) {
        var netOpt = nets.findById(id);
        if (netOpt.isEmpty()) return "redirect:/nets";

        var net = netOpt.get();

        if (net.getStatus() == GhostNet.Status.GEBORGEN
                || net.getStatus() == GhostNet.Status.VERSCHOLLEN) {
            return "redirect:/nets";
        }

        model.addAttribute("net", net);
        model.addAttribute("person", new Person());
        return "assign";
    }

    @PostMapping("/{id}/assign")
    public String assign(@PathVariable Long id, @ModelAttribute Person person, Model model) {
        var net = nets.findById(id).orElse(null);
        if (net == null) return "redirect:/nets";

        if (net.getStatus() == GhostNet.Status.GEBORGEN
                || net.getStatus() == GhostNet.Status.VERSCHOLLEN) {
            return "redirect:/nets";
        }

        if (person.getName() == null || person.getName().isBlank()) {
            model.addAttribute("net", net);
            model.addAttribute("person", person);
            model.addAttribute("error", "Bitte Name der bergenden Person angeben.");
            return "assign";
        }

        if (net.getSalvagedBy() != null) {
            model.addAttribute("net", net);
            model.addAttribute("person", person);
            model.addAttribute("error", "Für dieses Netz ist bereits eine bergende Person eingetragen.");
            return "assign";
        }

        var savedPerson = persons.save(person);
        net.setSalvagedBy(savedPerson);
        net.setReporterName(person.getName());
        net.setReporterPhone(person.getPhone());
        net.setAnonymousReport(false);
        net.setStatus(GhostNet.Status.BERGUNG_BEVORSTEHEND);

        nets.save(net);
        return "redirect:/nets";
    }

    @GetMapping("/{id}/done")
    public String markDone(@PathVariable Long id) {
        var net = nets.findById(id).orElseThrow();

        if (net.getStatus() == GhostNet.Status.GEBORGEN
                || net.getStatus() == GhostNet.Status.VERSCHOLLEN) {
            return "redirect:/nets";
        }

        net.setReporterName(null);
        net.setReporterPhone(null);
        net.setAnonymousReport(false);
        net.setStatus(GhostNet.Status.GEBORGEN);

        nets.save(net);
        return "redirect:/nets";
    }

    @GetMapping("/{id}/lost")
    public String lostForm(@PathVariable Long id, Model model) {
        var netOpt = nets.findById(id);
        if (netOpt.isEmpty()) return "redirect:/nets";

        var net = netOpt.get();

        if (net.getStatus() == GhostNet.Status.GEBORGEN
                || net.getStatus() == GhostNet.Status.VERSCHOLLEN) {
            return "redirect:/nets";
        }

        model.addAttribute("net", net);
        model.addAttribute("person", new Person());
        return "lost";
    }

    @PostMapping("/{id}/lost")
    public String markLost(@PathVariable Long id, @ModelAttribute Person person, Model model) {
        var netOpt = nets.findById(id);
        if (netOpt.isEmpty()) return "redirect:/nets";

        var net = netOpt.get();

        if (net.getStatus() == GhostNet.Status.GEBORGEN
                || net.getStatus() == GhostNet.Status.VERSCHOLLEN) {
            return "redirect:/nets";
        }

        if (person.getName() == null || person.getName().isBlank()) {
            model.addAttribute("net", net);
            model.addAttribute("person", person);
            model.addAttribute("error", "Bitte Name der meldenden Person angeben.");
            return "lost";
        }

        net.setAnonymousReport(false);
        net.setReporterName(person.getName());
        net.setReporterPhone(person.getPhone());
        net.setStatus(GhostNet.Status.VERSCHOLLEN);

        nets.save(net);
        return "redirect:/nets";
    }

    @GetMapping("/open")
    public String listOpen(Model model) {
        model.addAttribute("nets",
                nets.findAll().stream()
                        .filter(n -> n.getStatus() == GhostNet.Status.GEMELDET
                                || n.getStatus() == GhostNet.Status.BERGUNG_BEVORSTEHEND)
                        .toList());
        return "list";
    }

    @GetMapping("/")
    public String redirectRoot() {
        return "redirect:/nets";
    }
}
