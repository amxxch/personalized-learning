import { Link } from "react-router-dom";
import { FaUser } from "react-icons/fa";

const NavBar = () => {

    return (
        <div className="navbar bg-base-300 shadow-sm">
            <div className="flex-1">
                <Link to="/" className="btn btn-ghost text-xl">LearningBot</Link>
            </div>
            <div className="flex-none">
                <div className="dropdown dropdown-end">
                    <div tabIndex={0} role="button" className="btn btn-ghost btn-circle avatar">
                        <FaUser className='text-2xl'/>
                    </div>
                    <ul
                        tabIndex={0}
                        className="menu menu-sm dropdown-content bg-base-100 rounded-box text-center text-xl z-1 mt-3 w-32 p-2 shadow">
                        <li><Link to="/">Profile</Link></li>
                        <li><Link to="/">Progress</Link></li>
                        <li><Link to="/">Logout</Link></li>
                    </ul>
                </div>
            </div>
        </div>
    )
}

export default NavBar
