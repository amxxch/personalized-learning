import React, { ReactNode } from 'react'
import Layout from '../Layout';
import PrivateRoute from '../routes/PrivateRoute';

interface RouteWithLayoutProps {
    children: ReactNode;
    isPrivate?: boolean;
}

const RouteWithLayout = ({ children, isPrivate = false} : RouteWithLayoutProps) => {
  return (
    isPrivate ?
        <PrivateRoute>
            <Layout>
                {children}
            </Layout>
        </PrivateRoute> 
         : 
        <Layout>
            {children}
        </Layout>
    
  )
}

export default RouteWithLayout
