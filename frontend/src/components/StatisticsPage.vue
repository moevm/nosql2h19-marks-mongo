<template>
    <div>
        <faculties-average></faculties-average>
        <semester-marks></semester-marks>
        <course-marks></course-marks>
    </div>
</template>

<script>
    import FacultiesAverage from "./statistic/FacultiesAverage";
    import SemesterMarks from "./statistic/SemesterMarks";
    import CourseMarks from "./statistic/CourseMarks";

    export default {
        name: "StatisticsPage",
        components: {CourseMarks, SemesterMarks, FacultiesAverage},
    }
</script>

<style scoped>

</style>