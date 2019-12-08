<template>
    <div class="row mt-5 w-100">
        <div class="col-md-6 offset-3">
            <h3 class="m-4">
                Semester Marks
            </h3>
            <b-form ref="form" @submit="onSubmit">
                <b-form-group
                        :state="yearState"
                        label="year"
                        label-for="year-input"
                        invalid-feedback="Year is required"
                >
                    <b-form-input
                            ref="year"
                            id="year-input"
                            v-model="year"
                            :state="yearState"
                            required
                    ></b-form-input>
                </b-form-group>
                <b-form-group
                        :state="periodState"
                        label="period"
                        label-for="period-input"
                        invalid-feedback="Period is required"
                >
                    <b-form-input
                            ref="period"
                            id="period-input"
                            v-model="period"
                            :state="periodState"
                            required
                    ></b-form-input>
                </b-form-group>
                <b-button type="submit" variant="primary">Submit</b-button>
            </b-form>
            <div class="hello mt-5" ref="chartdiv">
            </div>
        </div>
    </div>
</template>

<script>
    import * as am4core from "@amcharts/amcharts4/core";
    import * as am4charts from "@amcharts/amcharts4/charts";
    import am4themes_animated from "@amcharts/amcharts4/themes/animated";
    import am4themes_kelly from "@amcharts/amcharts4/themes/kelly";

    am4core.useTheme(am4themes_animated);
    am4core.useTheme(am4themes_kelly);

    export default {
        name: "SemesterMarks",

        data (){
            return {
                data: null,
                year : '',
                period : '',
                yearState : null,
                periodState : null,
                errorMessage : null,
            }
        },

        beforeDestroy() {
            if (this.chart) {
                this.chart.dispose();
            }
        },

        methods: {
            onSubmit(event) {
                // Prevent modal from closing
                event.preventDefault();
                this.errorMessage = null;

                if (!this.checkFormValidity()) {
                    return;
                }

                this.$client({
                    method: 'get',
                    url: '/statistic/semester_marks',
                    params: {
                        year: this.year,
                        period: this.period,
                    }
                })
                .then((res) => (this.initializeGraphic(res)))
            },

            initializeGraphic: function (res) {
                this.data = res.data.sort((a, b) => a.mark > b.mark ? 1 : -1);

                var chartNew = am4core.create(this.$refs.chartdiv, am4charts.XYChart);

                chartNew.marginRight = 400;

                chartNew.data = this.data;

                var categoryAxis = chartNew.xAxes.push(new am4charts.CategoryAxis());
                categoryAxis.dataFields.category = "mark";
                categoryAxis.title.text = "Marks";


                var valueAxis = chartNew.yAxes.push(new am4charts.ValueAxis());
                valueAxis.title.text = "Amount";

                var series = chartNew.series.push(new am4charts.ColumnSeries());
                series.dataFields.valueY = "boys";
                series.dataFields.categoryX = "mark";
                series.name = "Boys";
                series.tooltipText = "{name}: [bold]{valueY}[/]";
                series.stacked = true;

                var series2 = chartNew.series.push(new am4charts.ColumnSeries());
                series2.dataFields.valueY = "girls";
                series2.dataFields.categoryX = "mark";
                series2.name = "Girls";
                series2.tooltipText = "{name}: [bold]{valueY}[/]";
                series2.stacked = true;

                // Add cursor
                chartNew.cursor = new am4charts.XYCursor();
            },

            checkFormValidity() {
                let validYear = this.$refs.year.checkValidity();
                if (!Number(this.year))
                    validYear = false;
                this.yearState = validYear;

                let validPeriod = this.$refs.period.checkValidity();
                if (this.period != 'spring' && this.period != 'autumn') {
                    validPeriod = false;
                }
                this.periodState = validPeriod;

                return validYear && validPeriod;
            }
        }
    }
</script>

<style scoped>
    .hello {
        height: 300px;
    }
</style>