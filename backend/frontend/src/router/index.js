import VueRouter from 'vue-router';
import IndexPage from "../components/IndexPage";
import StudentSearch from "../components/StudentSearch";
import GroupSearch from "../components/GroupSearch";
import FacultySearch from "../components/FacultySearch";
import DepartmentSearch from "../components/DepartmentsSearch";
import CoursesSearch from "../components/CoursesSearch";
import SemesterSearch from "../components/SemesterSearch";
import StatisticsPage from "../components/StatisticsPage";

const routes = [
    {
        path: '',
        component: IndexPage
    },
    {
        path: '/students',
        component: StudentSearch
    },
    {
        path: '/groups',
        component: GroupSearch
    },
    {
        path: '/faculties',
        component: FacultySearch
    },
    {
        path: '/departments',
        component: DepartmentSearch
    },
    {
        path: '/courses',
        component: CoursesSearch
    },
    {
        path: '/semesters',
        component: SemesterSearch
    },
    {
        path:'/stats',
        component: StatisticsPage
    }
];

const router = new VueRouter({
    mode: 'history',
    routes
});

export default router;