<template>
    <div class="row mt-5 w-100">
        <div class="col-md-6 offset-3">
            <h3 class="m-4">
                Course Marks
            </h3>
            <b-form ref="form" @submit="onSubmit">
                <b-form-group
                        :state="courseState"
                        label="course"
                        label-for="course-input"
                        invalid-feedback="Course is required"
                >
                    <b-form-input
                            ref="course"
                            id="course-input"
                            v-model="course"
                            :state="courseState"
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
        name: "CourseMarks",

        data (){
            return {
                data: null,
                course : '',
                courseState : null,
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
                    url: '/statistic/course_marks',
                    params: {
                        course: this.course,
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
                let validCourse = this.$refs.course.checkValidity();
                this.courseState = validCourse;

                return validCourse;
            }
        }
    }
</script>

<style scoped>
    .hello {
        height: 300px;
    }
</style>