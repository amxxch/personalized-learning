import { Link } from "react-router-dom";
import { useLocation } from 'react-router-dom';
import { FaUser } from "react-icons/fa";
import { useAuth } from "../context/AuthContext";

const NavBar = () => {
    const { pathname } = useLocation();
    const disabledHref = pathname.includes('/chat') || pathname.includes('/profile-setup')
    const { logout } = useAuth();

    return (
        <div className="navbar bg-base-300 shadow-sm">
            <div className="flex-1">
                {disabledHref ? (
                    <span className="btn btn-ghost text-xl font-mono">LearningBot</span>
                ) : (
                    <Link to="/" className="btn btn-ghost text-xl font-mono">LearningBot</Link>
                )}
            </div>
            <div className="flex-none">
                <div className="dropdown dropdown-end">
                    <div tabIndex={0} role="button" className="btn btn-ghost btn-circle avatar">
                        <FaUser className='text-2xl'/>
                    </div>
                    {disabledHref ? (
                    <ul
                    tabIndex={0}
                    className="menu menu-sm dropdown-content bg-base-100 rounded-box text-center text-xl z-1 mt-3 w-32 p-2 shadow">
                        <li><Link to='/profile'>Profile</Link></li>
                        <li><Link to="/" onClick={(e) => { e.preventDefault(); logout(); }}>Logout</Link></li>
                    </ul>
                    ) : (
                    <ul
                        tabIndex={0}
                        className="menu menu-sm dropdown-content bg-base-100 rounded-box text-center text-xl z-1 mt-3 w-32 p-2 shadow">
                        <li><Link to="/profile">Profile</Link></li>
                        <li><Link to="/" onClick={(e) => { e.preventDefault(); logout(); }}>Logout</Link></li>
                        
                    </ul>
                    )}
                </div>
            </div>
        </div>
    )
}

export default NavBar
