package org.jetbrains.kotlinx.dataframe.examples.springboot.web

import org.jetbrains.kotlinx.dataframe.examples.springboot.service.ReportService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ReportController(
    private val reportService: ReportService
) {
    @GetMapping("/")
    fun index(): String = "index"

    @GetMapping("/customers")
    fun customers(model: Model): String {
        val df = reportService.customersSortedByName()
        model.addAttribute("table", df.toTableView())
        model.addAttribute("title", "Customers (sorted by name)")
        return "table"
    }

    @GetMapping("/customers/filter")
    fun customersFilter(
        @RequestParam("country") country: String,
        model: Model
    ): String {
        val df = reportService.customersFilteredByCountry(country)
        model.addAttribute("table", df.toTableView())
        model.addAttribute("title", "Customers from $country")
        return "table"
    }

    @GetMapping("/sales")
    fun sales(model: Model): String {
        val df = reportService.salesSortedByValueDesc()
        model.addAttribute("table", df.toTableView())
        model.addAttribute("title", "Sales (sorted by value desc)")
        return "table"
    }
}
