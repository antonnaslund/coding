#==================================
# Gillespies algoritm
# Numeriska metoder och Simulering
#==================================
import numpy as np
from scipy.integrate import solve_ivp
import matplotlib.pyplot as plt
import random
import math

N = 1000
B = 0.3
R = 1/7
sim = 20

#Gillespies algoritm
def SSA(prop,stoch,X0,tspan,coeff):
    tvec=np.zeros(1)
    tvec[0]=tspan[0]
    Xarr=np.zeros([1,len(X0)])
    Xarr[0,:]=X0
    t=tvec[0]
    X=X0
    sMat=stoch()
    while t<tspan[1]:
        re=prop(X,coeff)
        a0=sum(re)
        if a0>1e-10:
            r1=random.random()
            r2=random.random()
            tau=-math.log(r1)/a0
            cre=np.cumsum(re)
            cre=cre/cre[-1]
            r=0
            while cre[r]<r2:
                r+=1
            t+=tau
            tvec=np.append(tvec,t) # time points in sim
            X=X+sMat[r,:]
            Xarr=np.vstack([Xarr,X]) # the state of system(su,in,re) in every time points
        else:
            print('Simulation ended at t=', t)
            return tvec, Xarr
            
            
    return tvec, Xarr

# Stochiometric matrix [susceptible, infected, recovered]
def stoch():
    return np.array([[-1, 1, 0],  #susceptible -> infected
                    [0, -1, 1]])  # infected -> recovered

def prop(X, coeff):
    B, R = coeff #array som innehåller infektionshastighet och återhämtningshastighet
    N = sum(X)
    re = np.zeros(2)
    #X[0] = calculates propensity for susceptible person to become infected
    re[0] = B * X[0] * X[1] / N  #Propensity for infection
    #X[1] = calculates the probability that an infected individual recovers
    re[1] = R * X[1]  #Propensity for recovery
    return re

def MC_simulation(B, R, N):
    X0 = [N-5, 5, 0]
    tspan = [0, 120]

    # Coefficients [B, R]
    coeff = [B, R]
    testList = []
    for i in range(sim):
        tvec, Xarr = SSA(prop, stoch, X0, tspan, coeff)
        testList.append(tvec)
        testList.append(Xarr)
    return testList

# Run the Gillespie simulation
MC_list = MC_simulation(B, R, N)

#Comparison with the deterministic model
def ODE(t, y):
    y_t = [-B * y[1] * y[0] / N, B * y[1] * y[0] / N - R * y[1], R * y[1]]
    return np.array(y_t)

y0 = [N-5, 5, 0]

# Time span
t_span = [0, 120]
t_eval = np.linspace(0, 120, 1000)
sol = solve_ivp(ODE, t_span, y0, t_eval=t_eval)


#Plot the deterministic model
plt.plot(sol.t, sol.y[0, :], label='Susceptible', color='black')
plt.plot(sol.t, sol.y[1, :], label='Infected', color='black')
plt.plot(sol.t, sol.y[2, :], label='Recovered', color='black')

#Plot the MC simulations
for i in range(sim): 
    plt.plot(MC_list[2*i], MC_list[2*i+1])

# Plotting the results
plt.xlabel('Time [s]')
plt.ylabel('Number of people')
plt.title('SIR-model using Gillespie\'s Algorithm')
plt.legend()
plt.show()



# # Gillespies algoritm
# #==================================
# # Numeriska metoder och Simulering
# #==================================
# import numpy as np
# from scipy.integrate import solve_ivp
# import matplotlib.pyplot as plt
# import random
# import math

# N = 1000
# B = 0.3
# R = 1/7

# #Gillespies algoritm
# def SSA(prop,stoch,X0,tspan,coeff):
#     tvec=np.zeros(1)
#     tvec[0]=tspan[0]
#     Xarr=np.zeros([1,len(X0)])
#     Xarr[0,:]=X0
#     t=tvec[0]
#     X=X0
#     sMat=stoch()
#     while t<tspan[1]:
#         re=prop(X,coeff)
#         a0=sum(re)
#         if a0>1e-10:
#             r1=random.random()
#             r2=random.random()
#             tau=-math.log(r1)/a0
#             cre=np.cumsum(re)
#             cre=cre/cre[-1]
#             r=0
#             while cre[r]<r2:
#                 r+=1
#             t+=tau
#             tvec=np.append(tvec,t)
#             X=X+sMat[r,:]
#             Xarr=np.vstack([Xarr,X])
#         else:
#             print('Simulation ended at t=', t)
#             return tvec, Xarr
            
            
#     return tvec, Xarr

# #Initialvärden, givet uppgift[susceptible, infected, recovered]
# X0 = [N-5, 5, 0]

# # Time span
# tspan = [0, 120]

# # Coefficients [B, R]
# coeff = [B, R]

# #Stochiometric matrix [susceptible, infected, recovered]
# def stoch():
#     return np.array([[-1, 1, 0],  #susceptible -> infected
#                     [0, -1, 1]])  # infected -> recovered

# def prop(X, coeff):
#     B, R = coeff #array som innehåller infektionshastighet och återhämtningshastighet
#     N = sum(X)
#     re = np.zeros(2)

#     #X[0] = calculates propensity for susceptible person to become infected
#     re[0] = B * X[0] * X[1] / N  #Propensity for infection
#     #X[1] = calculates the probability that an infected individual recovers
#     re[1] = R * X[1]  #Propensity for recovery
#     return re


# # Run the Gillespie simulation
# tvec, Xarr = SSA(prop, stoch, X0, tspan, coeff)

# # Plotting the results
# plt.figure()
# plt.plot(tvec, Xarr[:, 0], label='Susceptible', color='blue')
# plt.plot(tvec, Xarr[:, 1], label='Infectious', color='black')
# plt.plot(tvec, Xarr[:, 2], label='Recovered/Resistant', color='red')
# plt.xlabel('Time [s]')
# plt.ylabel('Number of people')
# plt.title('SIR-model using Gillespie\'s Algorithm')
# plt.legend()
# plt.show()