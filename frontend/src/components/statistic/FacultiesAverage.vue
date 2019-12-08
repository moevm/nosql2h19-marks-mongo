<template>
    <div class="row mt-5 w-100">
        <div class="col-md-6 offset-md-3">
            <h3 class="mb-4">
                Faculties average marks
            </h3>
            <div class="hello" ref="chartdiv">
            </div>
        </div>
    </div>
</template>

<script>
    import * as am4core from "@amcharts/amcharts4/core";
    import * as am4charts from "@amcharts/amcharts4/charts";

    export default {
        name: "FacultiesAverage",

        data() {
            return {
                data: null
            }
        },

        mounted() {
            this.$client
                .get('/statistic/faculty_average')
                .then(response => this.initializeGraphic(response))
        },

        methods:{
            initializeGraphic(res) {
                this.data = res.data;

                let chart = am4core.create(this.$refs.chartdiv, am4charts.XYChart);

                this.data.map(el => el.average = Number(el.average));

                chart.data = this.data;

                let categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
                categoryAxis.dataFields.category = "faculty";
                categoryAxis.title.text = "Faculties";

                let valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
                valueAxis.title.text = "Average";

                let series = chart.series.push(new am4charts.StepLineSeries());
                series.name = "Marks average";
                series.dataFields.valueY = "average";
                series.dataFields.categoryX = "faculty";
                series.strokeWidth = 3;

                series.noRisers = true;

                var bullet = series.bullets.push(new am4charts.CircleBullet());
                bullet.fill = am4core.color("white");
                bullet.strokeWidth = 3;

                this.chart = chart;
            },
        },

        beforeDestroy() {
            if (this.chart) {
                this.chart.dispose();
            }
        },
    }
</script>

<style scoped>
    .hello {
        height: 300px;
    }
</style>